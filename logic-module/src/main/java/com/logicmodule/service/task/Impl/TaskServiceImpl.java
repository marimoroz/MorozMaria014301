package com.logicmodule.service.task.Impl;

import com.datamodule.dto.EmployeeDTO;
import com.datamodule.dto.SequentialTaskNodeDTO;
import com.datamodule.dto.TaskCreateDTO;
import com.datamodule.dto.TaskDTO;
import com.datamodule.exeptions.ModelNotFound;
import com.datamodule.models.Document;
import com.datamodule.models.Employee;
import com.datamodule.models.SequentialTaskNode;
import com.datamodule.models.Task;
import com.datamodule.models.enums.ETypeTaskExecute;
import com.datamodule.models.enums.TaskMessage;
import com.datamodule.repository.SequentialTaskNodeRepository;
import com.datamodule.repository.TaskRepository;
import com.file.exceptions.FileException;
import com.file.service.FileCRUD;
import com.logicmodule.exeptions.EDSException;
import com.logicmodule.exeptions.NotificationException;
import com.logicmodule.exeptions.TaskException;
import com.logicmodule.mappers.EmployeeMapper;
import com.logicmodule.mappers.SequentialTaskNodeMapper;
import com.logicmodule.mappers.TaskMapper;
import com.logicmodule.service.document.DocumentOperation;
import com.logicmodule.service.eds.EdsService;
import com.logicmodule.service.notification.NotificationOperation;
import com.logicmodule.service.task.TaskService;
import com.logicmodule.service.task.node.NodeOperation;
import com.logicmodule.service.task.node.NodeService;
import com.logicmodule.service.user.EmployService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service(value = "TaskServiceImpl")
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final NodeOperation nodeOperation;

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final DocumentOperation documentOperation;

    private final FileCRUD fileCRUD;

    private final EmployeeMapper employeeMapper;

    private final EmployService employService;

    private final SequentialTaskNodeRepository sequentialTaskNodeRepository;

    private final NotificationOperation notificationOperation;

    private final NodeService nodeService;

    private final SequentialTaskNodeMapper sequentialTaskNodeMapper;

    @Override
    @Transactional
    public TaskDTO createTask(TaskCreateDTO taskDTO) throws ModelNotFound,
            NotificationException, FileException, IOException {
        final String taskDirectory = UUID.randomUUID() + taskDTO.getNameTask();
        LinkedList<EmployeeDTO> employees;
        LinkedList<Employee> employeesModel = new LinkedList<>();

        fileCRUD.createDirectory(taskDirectory);

        LinkedList<SequentialTaskNode> created = createNodes(taskDTO, employeesModel);

        var document = documentOperation.findDocumentByIDWithTasks(taskDTO.getId_document());
        var file = fileCRUD.getFile(document.getFileName(), document.getFileDirectory());
        var docCopy = documentOperation.createCopyDocumentToArchive(document, file);
        var task = Task.builder()
                .nameTask(taskDTO.getNameTask())
                .directory_path(taskDirectory)
                .comment(taskDTO.getComment())
                .task_publish(taskDTO.getTask_publish())
                .taskExecute(ETypeTaskExecute.WAIT)
                .documentWait(document)
                .documentExecute(null)
                .documentDone(null)
                .startDocument(docCopy)
                .employee(employService
                        .getEmployeeModelById(taskDTO
                                .getId_employee_creator()))
                .build();
        Task finalTask = task;
        created.forEach(
                sequentialTaskNode -> sequentialTaskNode.setTask(finalTask)
        );
        task.setSequentialTaskNodes(created);

        task = taskRepository.save(task);

        addTaskToEndDocumentTasksWait(task, document);

        task = taskRepository.save(task);

        employees = employeeMapper.toDTOForLinkedList(employeesModel);
        var taskDTOResponse = taskMapper.toDTO(task);

        taskDTOResponse.setEmployees(employees);

        notificationOperation.notifyEmployeesAboutSequenceNode(created.stream()
                        .map(sequentialTaskNodeMapper::toDTO)
                        .collect(Collectors.toCollection(LinkedList::new)), "add",
                employees.stream().map(EmployeeDTO::getIdUser)
                        .collect(Collectors.toCollection(LinkedList::new)),
                TaskMessage.NEW_NODE_TASK);

        taskDTOResponse.setStarter(employService
                .getEmployeeById(taskDTO.getId_employee_creator()));
        return taskDTOResponse;
    }

    @Override
    @Transactional
    public TaskDTO startTaskExecute(Long id_task, Long id_document) throws ModelNotFound,
            TaskException {
        var task = findTaskByID(id_task);
        var doc = documentOperation.findDocumentByIDWithTasks(id_document);
        if (task.getDocumentWait() != null
                && doc.getExecuteTask() == null &&
                new LinkedList<>(doc.getWaitTasks()).getFirst().getIdTask().equals(id_task)) {

            setTaskToExecute(task, task.getDocumentWait());
            task = taskRepository.save(task);
            // notification for start task
            notificationOperation.delegateNotification(task
                            .getSequentialTaskNodes().get(0)
                            .getEmployee()
                            .getUser()
                    , TaskMessage.DO_NODE_TASK,
                    -1L, task.getSequentialTaskNodes()
                            .get(0).getIdSequentialTaskNode());


        } else {
            throw new TaskException("can not start task with id:" + id_task);
        }
        return convertTaskToTaskDTO(task);
    }

    public LinkedList<Task> findTasksByIdDocumentWithStatusRoll_Back(Long id_document) {
        return taskRepository
                .findTasksWithSequentialTaskNodesByDocumentId(id_document,
                        ETypeTaskExecute.ROLL_BACK);
    }

    @Override
    @Transactional
    public TaskDTO finishTaskExecute(Long id_task, Long id_document)
            throws ModelNotFound, TaskException, FileException, IOException, EDSException {
        var task = findTaskByID(id_task);
        var publicKey = task.getSequentialTaskNodes().stream()
                .map(sequentialTaskNode ->
                {
                    var key = sequentialTaskNode.getEmployee().getUser().getPublishKey()
                            .getPublishKey();
                    try {
                        return EdsService.bytesToPublicKey(key);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toCollection(LinkedList::new)).getLast();
        var username = task.getSequentialTaskNodes().stream().
                map(sequentialTaskNode ->
                        sequentialTaskNode.getEmployee().getUser().getUsername())
                .collect(Collectors.toCollection(LinkedList::new)).getLast();
        var lastNodeDoc = new LinkedList<>(task.getSequentialTaskNodes()).getLast().getDocumentState();
        var doc = documentOperation.findDocumentByIDWithTasks(id_document);
        log.info (lastNodeDoc.getFileName());
        log.warn(lastNodeDoc.getFileDirectory());
        var doc_file = fileCRUD.getFile(lastNodeDoc.getFileName(), lastNodeDoc.getFileDirectory());

        if (doc.getExecuteTask().getIdTask().equals(id_task)) {
            task.setDocumentDone(doc);
            task.setDocumentExecute(null);
            task.setTaskExecute(ETypeTaskExecute.DONE);
            task = taskRepository.save(task);
            var pair = EdsService
                    .splitDocumentAndSignature(doc_file.get(), username);
             { log.info (task.getDocumentDone().getFileName());
                 log.warn(task.getDocumentDone().getFileDirectory());
                 fileCRUD.deleteFile(task.getDocumentDone().getFileDirectory(),
                         task.getDocumentDone().getFileName());
                 fileCRUD.saveFile(doc_file.get(),task.getDocumentDone().getFileDirectory(),
                         task.getDocumentDone().getFileName(),"docx");
                notificationOperation.delegateNotification(task
                                .getEmployee()
                                .getUser()
                        , TaskMessage.FINISH_TASK,
                        -1L, task.getSequentialTaskNodes()
                                .get(0).getIdSequentialTaskNode());

            }
        } else {
            throw new TaskException("can not finish task with id:" + id_task);
        }
        return convertTaskToTaskDTO(task);
    }

    private void setTaskToExecute(Task task,
                                  Document document) {
        task.setDocumentExecute(document);
        task.setDocumentWait(null);
        task.setDocumentDone(null);
        task.setTaskExecute(ETypeTaskExecute.EXECUTE);
        if (!task.getSequentialTaskNodes().isEmpty()) {
            task.getSequentialTaskNodes().get(0).setCan_be_done(true);
        }

    }

    private void linkedForBdTask(LinkedList<Task> created, long initialCount) {
        long count = initialCount;
        Long prevNode = -1L;
        for (int i = 0; i < created.size(); i++) {
            Long nextNode = (i < created.size() - 1) ? created.get(i + 1).getIdTask() : -1L;
            var node = created.get(i);
            node.setNextTask(nextNode);
            node.setPrevTask(prevNode);
            node.setCount(count);
            prevNode = node.getIdTask();
            count++;
        }
    }

    private void addTaskToEndDocumentTasksWait(Task task, Document document) {
        Long next = -1L;
        Long prev = -1L;
        long count = 0L;
        var waitTasks = document.getWaitTasks();
        if (!document.getDoneTask().isEmpty() && document.getExecuteTask() == null) {
            var prevNode = new LinkedList<>(document.getDoneTask()).getLast();
            count = prevNode.getCount() + 1;
            prev = prevNode.getIdTask();
            prevNode.setNextTask(task.getIdTask());
        } else if (document.getExecuteTask() != null) {
            var prevNode = document.getExecuteTask();
            count = prevNode.getCount() + 1;
            prev = prevNode.getIdTask();
            prevNode.setNextTask(task.getIdTask());
        }

        if (!waitTasks.isEmpty()) {
            var prevNode = new LinkedList<>(waitTasks).getLast();
            prevNode.setNextTask(task.getIdTask());
        }
        linkTask(task, next, prev, count, new LinkedList<>(waitTasks));
        document.setWaitTasks(waitTasks);
    }


    private void linkTask(Task task, Long next, Long prev,
                          long count, LinkedList<Task> tasks) {
        if (!tasks.isEmpty()) {
            prev = tasks.getLast().getIdTask();
            count = tasks.getLast().getCount() + 1;
            tasks.getLast().setNextTask(task.getIdTask());
        }
        task.setNextTask(next);
        task.setPrevTask(prev);
        task.setCount(count);
    }

    @Override
    @Transactional
    public TaskDTO updateTask(TaskCreateDTO taskCreateDTO) throws ModelNotFound,
            TaskException, NotificationException {
        var task = findTaskByID(taskCreateDTO.getIdTask());
        if (task.getTaskExecute().equals(ETypeTaskExecute.WAIT)) {
            var employees = new LinkedList<Employee>();
            task.getSequentialTaskNodes().
                    forEach(
                            sequentialTaskNode -> {
                                notificationOperation
                                        .acceptNotificationSequentialTaskNode
                                                (sequentialTaskNode.getIdSequentialTaskNode());
                            }

                    );
            var nodesToDelete = task.getSequentialTaskNodes().stream()
                    .map(SequentialTaskNode::getIdSequentialTaskNode).collect(Collectors.toCollection(ArrayList::new));
            sequentialTaskNodeRepository.deleteAllById(nodesToDelete);
            LinkedList<SequentialTaskNode> created = createNodes(taskCreateDTO, employees);
            taskMapper.update(task, taskCreateDTO);
            Task finalTask = task;
            created.forEach(
                    sequentialTaskNode -> sequentialTaskNode.setTask(finalTask)
            );
            task.setSequentialTaskNodes(created);
            task = taskRepository.save(task);


            task.setSequentialTaskNodes(created);
            task = taskRepository.save(task);

            var employeeDTOS = employeeMapper.toDTOForLinkedList(employees);

            notificationOperation.notifyEmployeesAboutSequenceNode(created.stream()
                            .map(sequentialTaskNodeMapper::toDTO)
                            .collect(Collectors.toCollection(LinkedList::new)), "add",
                    employeeDTOS.stream().map(EmployeeDTO::getIdUser)
                            .collect(Collectors.toCollection(LinkedList::new)),
                    TaskMessage.NEW_NODE_TASK);

        } else {
            throw new TaskException("this task not update");
        }
        return convertTaskToTaskDTO(task);
    }

    private LinkedList<SequentialTaskNode> createNodes(TaskCreateDTO taskCreateDTO, LinkedList<Employee> employees) {
        AtomicLong count = new AtomicLong();
        LinkedList<SequentialTaskNode> created = taskCreateDTO.getId_employee().stream()
                .map(id_employee ->
                {
                    SequentialTaskNode node;
                    try {
                        node = nodeOperation
                                .createSequentialTaskNode(id_employee, -1L,
                                        -1L, taskCreateDTO.getNode_name().get((int) count.get())
                                        , taskCreateDTO.getName_desc().get((int) count.get()), employees, count.get());
                    } catch (ModelNotFound e) {
                        throw new RuntimeException(e);
                    }
                    count.getAndIncrement();
                    return sequentialTaskNodeRepository.save(node);
                }).collect(Collectors.toCollection(LinkedList::new));

        nodeService.linkedForBdNodes(created);
        return created;
    }

    @Override
    @Transactional
    public Page<TaskDTO> changeOfPriorityInWaitTasks(LinkedList<Long> idTaskUpdate,
                                                     Long id_document_wait, Pageable pageable) throws
            TaskException {
        var tasksWait = taskRepository.findWaitTasksWithSequentialTaskNodes(id_document_wait);
        var tasksDone = taskRepository.findDoneTasksWithSequentialTaskNodes(id_document_wait);
        var taskExecute = taskRepository.findExecuteTaskByIdDocument(id_document_wait, ETypeTaskExecute.EXECUTE)
                .orElse(null);
        long count = tasksWait.getFirst().getCount();
        var linkedUpdate = new LinkedList<>(tasksWait);
        if (tasksWait.size() != idTaskUpdate.size()) {
            throw new TaskException("change of priority not done");
        }
        for (Task t : tasksWait) {
            if (!idTaskUpdate.contains(t.getIdTask())) {
                throw new TaskException("change of priority not done");
            }
            int index = idTaskUpdate.indexOf(t.getIdTask());
            linkedUpdate.set(index, t);
        }

        linkedForBdTask(linkedUpdate, count);

        if (taskExecute != null) {
            taskExecute.setNextTask(linkedUpdate.getFirst().getIdTask());
            linkedUpdate.getFirst().setPrevTask(taskExecute.getIdTask());
            taskRepository.save(taskExecute);
        }
        if (!tasksDone.isEmpty() && taskExecute == null) {
            tasksDone.getLast().setNextTask(linkedUpdate.getFirst().getIdTask());
            linkedUpdate.getFirst().setPrevTask(tasksDone.getLast().getIdTask());
            taskRepository.saveAll(tasksDone);
        }
        taskRepository.saveAll(linkedUpdate);


        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, linkedUpdate.size());
        var taskDTOLinkedList = linkedUpdate.subList(start, end)
                .stream().map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return new PageImpl<>(taskDTOLinkedList, pageable, linkedUpdate.size());
    }

    @Override
    @Transactional
    public void rollbackToTask(Long id_task, Long id_document) throws ModelNotFound,
            TaskException, FileException, IOException {
        var document = documentOperation
                .findDocumentByIDWithTasks(id_document);
        var tasks = document.getDoneTask().stream()
                .collect(Collectors.toMap(Task::getIdTask, task -> task,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
        var idsTasks = document.getDoneTask().stream().map(Task::getIdTask)
                .collect(Collectors.toCollection(LinkedList::new));
        log.error(String.valueOf(idsTasks.size()));
        if (document.getDoneTask().isEmpty() || !idsTasks.contains(id_task)) {
            throw new TaskException("no rollback");
        }
        if (document.getExecuteTask() != null) {
            throw new TaskException("pleas wait for ongoing processes to complete");
        }
        long count = 0;
        int increment = -2;
        for (int i = idsTasks.size() - 1; i >= 0; i--) {
            count = idsTasks.get(i);
            var task = tasks.get(count);
            task.setTaskExecute(ETypeTaskExecute.ROLL_BACK);
            task.setPrevTask(-1L);
            task.setDocumentDone(null);
            task.setNextTask(-1L);
            taskRepository.save(task);
            increment = i;
            if (count == id_task) {
                break;
            }
        }
        FileItem fileRollBack;
        if (increment == 0) {
            var docTask = tasks.get(count).getStartDocument();
            fileRollBack = fileCRUD.getFile(docTask.getFileName(),
                    docTask.getFileDirectory());

            fileCRUD.deleteFile(document.getFileName(),document.getFileDirectory());
            fileCRUD.saveFile(fileRollBack.get(), document.getFileDirectory(),
                    document.getFileName(), "docx");

            Task taskPrev = null;
            if (document.getExecuteTask() != null) {
                taskPrev = document.getExecuteTask();
                taskPrev.setPrevTask(-1L);

            }
            if (document.getExecuteTask() == null && !document.getWaitTasks().isEmpty()) {
                taskPrev = new LinkedList<>(document.getWaitTasks()).getFirst();
                taskPrev.setPrevTask(-1L);

            }
            if (taskPrev != null) {
                taskRepository.save(taskPrev);
            }
        } else {
            increment--;
            long rollbackDocumentIdTask = idsTasks.get(increment);
            var taskPrev = tasks.get(rollbackDocumentIdTask);
            var documentState = taskPrev.getStartDocument();
            fileRollBack = fileCRUD.getFile(documentState.getFileName(),
                    documentState.getFileDirectory());
            fileCRUD.deleteFile(document.getFileName(),document.getFileDirectory());
            fileCRUD.saveFile(fileRollBack.get(), document.getFileDirectory(),
                    document.getFileName(), "docx");
            Task taskUpdate = null;
            if (document.getExecuteTask() != null) {
                taskUpdate = document.getExecuteTask();
                taskUpdate.setPrevTask(taskPrev.getIdTask());
                taskPrev.setNextTask(taskUpdate.getIdTask());
                taskRepository.save(taskPrev);
            }
            if (document.getExecuteTask() == null && !document.getWaitTasks().isEmpty()) {
                taskUpdate = new LinkedList<>(document.getWaitTasks()).getFirst();
                taskUpdate.setPrevTask(taskPrev.getIdTask());
                taskPrev.setNextTask(taskUpdate.getIdTask());
                taskRepository.save(taskPrev);
            }
            if (taskUpdate != null) {
                taskRepository.save(taskUpdate);
            }
        }
    }

    @Override
    @Transactional
    public void deleteTaskById(Long id_task) throws ModelNotFound, TaskException {
        var task = findTaskByID(id_task);
        if (task.getTaskExecute().equals(ETypeTaskExecute.WAIT)) {
            taskRepository.delete(task);
        } else {
            throw new TaskException("No delete task with id:" + id_task);
        }
    }

    @Override
    public TaskDTO getTaskDTO(Long id_task)
            throws ModelNotFound {
        return convertTaskToTaskDTO(findTaskByID(id_task));
    }

    @Override
    @Transactional
    public Page<TaskDTO> getExecuteTaskByDocumentId(Long id_document, Pageable pageable) {
        var tasks = taskRepository
                .findTasksWithSequentialTaskNodesByDocumentId(id_document, ETypeTaskExecute.EXECUTE)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, tasks);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getWaitTasksByDocumentId(Long id_document, Pageable pageable)
            throws ModelNotFound {
        var document = documentOperation.findDocumentByIdWithTasksWait(id_document);
        return getTasksWaitPage(pageable, document);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getDoneTasksByDocumentId(Long id_document, Pageable pageable)
            throws ModelNotFound {
        var document = documentOperation.findDocumentByIdWithTasksDone(id_document);
        return getTasksDonePage(pageable, document);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getExecuteTasks(Pageable pageable) {
        var tasks = taskRepository.findTasksWithSequentialTaskNodes(ETypeTaskExecute.EXECUTE)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, tasks);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getWaitTasks(Pageable pageable) {
        var tasks = taskRepository.findTasksWithSequentialTaskNodes(ETypeTaskExecute.WAIT)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, tasks);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getDoneTasks(Pageable pageable) {
        var tasks = taskRepository.findTasksWithSequentialTaskNodes(ETypeTaskExecute.DONE)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, tasks);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getWaitTasksByDocumentIdWithUserId(Long id_user, Pageable pageable) {
        var tasks = taskRepository.findTasksWithSequentialTaskNodesByUserId(id_user, ETypeTaskExecute.WAIT)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, tasks);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getDoneTasksByDocumentIdWithUserId(Long id_user, Pageable pageable) {
        var tasks = taskRepository.findTasksWithSequentialTaskNodesByUserId(id_user, ETypeTaskExecute.DONE)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        ;
        return getTasksPage(pageable, tasks);
    }

    @NotNull
    private Page<TaskDTO> getTasksWaitPage(Pageable pageable, Document document) {
        LinkedList<TaskDTO> taskDTOs = document.getWaitTasks().stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, taskDTOs);
    }

    @NotNull
    private Page<TaskDTO> getTasksDonePage(Pageable pageable, Document document) {
        LinkedList<TaskDTO> taskDTOs = document.getDoneTask().stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, taskDTOs);
    }

    @NotNull
    private Page<TaskDTO> getTasksPage(Pageable pageable, LinkedList<TaskDTO> taskDTOs) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int start = page * size;
        int end = Math.min(start + size, taskDTOs.size());
        return new PageImpl<>(taskDTOs.subList(start, end), pageable,
                taskDTOs.size());
    }


    public Task findTaskByID(Long id_task) throws ModelNotFound {
        return taskRepository.findTaskByIdTask(id_task)
                .orElseThrow(() -> new ModelNotFound("this task not found with id:"
                        + id_task));
    }

    public Task findTaskByIdDocument(Long id_document) throws ModelNotFound {
        return taskRepository.findTaskByIdDocument(id_document)
                .orElseThrow(() -> new ModelNotFound("this task not found with id_document:"
                        + id_document));
    }

    private TaskDTO convertTaskToTaskDTO(Task task) {
        var taskDTO = taskMapper.toDTO(task);
        var sequentialTaskNodeDTOS = new LinkedList<SequentialTaskNodeDTO>();
        var employList = new LinkedList<EmployeeDTO>();
        taskDTO.setStarter(employeeMapper.toDTO(task.getEmployee()));
        for (SequentialTaskNode s : task.getSequentialTaskNodes()) {
            employList.add(employeeMapper.toDTO(s.getEmployee()));
            sequentialTaskNodeDTOS.add(sequentialTaskNodeMapper.toDTO(s));
        }
        taskDTO.setEmployees(employList);
        taskDTO.setSequentialTaskNodeDTOS(sequentialTaskNodeDTOS);
        return taskDTO;
    }

    @Override
    @Transactional
    public Page<TaskDTO> getAcceptDoneTasks(Pageable pageable, Long id_document) {
        var taskAccept = taskRepository.findExecuteTasks(ETypeTaskExecute.DONE, id_document)
                .stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return getTasksPage(pageable, taskAccept);
    }

    @Override
    @Transactional
    public Page<TaskDTO> getAllTask(Pageable pageable) {
        var taskDTOs = taskRepository.findAll(pageable).stream()
                .map(this::convertTaskToTaskDTO)
                .collect(Collectors.toCollection(LinkedList::new));
        return new PageImpl<>(taskDTOs,pageable,taskDTOs.size());
    }
}
