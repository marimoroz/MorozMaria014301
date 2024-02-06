package com.file.service.Impl;

import com.file.exceptions.FileException;
import com.file.service.FileCRUD;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


@Slf4j
@Service("FileResourceImpl")
public class FileCRUDImpl implements FileCRUD {

    @Value("${allPath}")
    private String path;

    @Override
    public void createDirectory(String name) {
        Path directory_path = Paths.get(path + name);
        try {
            Files.createDirectories(directory_path);
            System.out.println("Directory created");
        } catch (IOException e) {
            System.err.println("Directory didn't create: " + e.getMessage());
        }
    }


    @Override
    public void saveFile(MultipartFile multipartFile, String directory) throws FileException {
        if (new File(path + directory + multipartFile.getOriginalFilename()).exists()) {
            throw new FileException("this file exist");
        }
        File save = new File(path + directory + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(save);
        } catch (IOException e) {
            throw new FileException("this file not exist");
        }
    }

    @Override
    public void saveFile(byte[] multipartFile, String directory, String filename, String filetype) throws FileException {
        try {
            if (new File(path + directory + filename).exists()) {
                throw new FileException("this file exist");
            }
            Path directoryPath = Paths.get(path + directory);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            Path filePath = directoryPath.resolve(filename);
            Files.write(filePath, multipartFile, StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new FileException("Failed to save the file: " + e.getMessage());
        }
    }


    @Override
    public void updateFile(MultipartFile multipartFile, String name, String directory) throws FileException, IOException {
        if (new File(path + directory + multipartFile.getOriginalFilename()).exists()) {
            throw new FileException("this file not exist");
        }
        if (!multipartFile.isEmpty()) {
            deleteFile(name, directory);
            saveFile(multipartFile, directory);
        }
    }


    @Override
    public void updateFile(FileItem multipartFile, String name, String directory) throws FileException, IOException {
        deleteFile(name, directory);
        saveFile(multipartFile.get(), directory, name, "docx");
    }
    @Override
    public void updateFile(byte[] newFileContent, String filename, String directory)
            throws FileException {
        try {
            Path filePath = Paths.get(path + directory + File.separator + filename);
            if (!Files.exists(filePath)) {
                throw new FileException("File not found: " + filename);
            }
            Files.write(filePath, newFileContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException | FileException e) {
            throw new FileException("Failed to update the file: " + e.getMessage());
        }
    }
    @Override
    public void deleteFile(String name, String directory) throws IOException {
        var file = new File(path + directory + File.separator + name);
        Files.deleteIfExists(file.toPath());
    }

    @Override
    public FileItem getFile(String name, String directory) throws FileException {
        log.info(path + directory + "\\" + name);
        File file = new File(path + directory + File.separator+ name);
        FileItem fileItem;
        try {
            fileItem = new DiskFileItem(file.getName(), Files.probeContentType(file.toPath()),
                    false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            input.close();
            os.close();
        } catch (IOException ex) {
            throw new FileException("this file not exist");
        }
        return fileItem;
    }
}
