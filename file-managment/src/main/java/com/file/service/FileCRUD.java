package com.file.service;


import com.file.exceptions.FileException;
import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileCRUD {

    void createDirectory(String name);

    void saveFile(MultipartFile multipartFile, String directory) throws FileException;

    void saveFile(byte[] multipartFile, String directory, String filename,String filetype) throws FileException;

    void updateFile(MultipartFile multipartFile, String name, String directory) throws FileException, IOException;

    void updateFile(byte[] newFileContent, String filename, String directory) throws FileException;
    void updateFile(FileItem multipartFile, String name, String directory) throws FileException, IOException;

    void deleteFile(String name, String directory) throws FileException, IOException;

    FileItem getFile(String name, String directory) throws IOException, FileException;

}
