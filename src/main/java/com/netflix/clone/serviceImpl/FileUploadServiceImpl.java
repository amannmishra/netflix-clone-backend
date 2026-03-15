package com.netflix.clone.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.netflix.clone.service.FileUploadService;
import com.netflix.clone.utils.FileHandlerUtil;

import jakarta.annotation.PostConstruct;

@Service
public class FileUploadServiceImpl implements FileUploadService{

     
    private Path videoStorageLocation;
    private Path imageStorageLocation;


    @Value("${file.upload.video-dir:uploads/videos}")
    private String videoDir;
     @Value("${file.upload.image-dir:uploads/images}")
    private String imageDir;

    @PostConstruct
    public void init(){
        this.videoStorageLocation=Paths.get(videoDir).toAbsolutePath().normalize();
        this.imageStorageLocation=Paths.get(imageDir).toAbsolutePath().normalize();
        try{
            Files.createDirectories(this.videoStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        }catch (Exception ex){
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.",ex); 
        }
            
        }
    @Override
    public String storeVideoFile(MultipartFile file) {
      return storeFile(file,videoStorageLocation);
    }
      @Override
    public String storeImageFile(MultipartFile file) {
     return storeFile(file, imageStorageLocation);
    }
    private String storeFile(MultipartFile file, Path StorageLocation) {
      String fileExtension=FileHandlerUtil.extractFileExtension(file.getOriginalFilename());
      String uuid=UUID.randomUUID().toString();
      String filename=uuid + fileExtension;
      try{
        if (file.isEmpty()){
            throw new RuntimeException("Failed to store empty file"+filename);
        }
        Path targetLocation=StorageLocation.resolve(filename);
        Files.copy(file.getInputStream(),targetLocation,StandardCopyOption.REPLACE_EXISTING);
        return uuid;
            
        }catch(IOException ex){
            throw new RuntimeException("Failed to store file"+filename,ex);
        }
      }
    @Override
    public ResponseEntity<Resource> serveVideo(String uuid, String rangeHeader) {
      try{
        Path filePath=FileHandlerUtil.findFileByUuid(videoStorageLocation, uuid);
        Resource resource=FileHandlerUtil.createFullResource(filePath);

        String fileName=resource.getFilename();
        String contentType=FileHandlerUtil.detectVideoContentType(fileName);
        Long fileLength=resource.contentLength();

        if (isFullContentRequest(rangeHeader)) {
          return buildFullVideoResponse(resource,contentType,fileName,fileLength);
        }
        return buildPartialVideoResponse(filePath,rangeHeader,contentType,fileName,fileLength);
    }
    catch(Exception e ){
      return ResponseEntity.notFound().build();

      }
    }
  
    private boolean isFullContentRequest(String rangeHeader) {
     return rangeHeader==null||rangeHeader.isEmpty();
    }
     private ResponseEntity<Resource> buildFullVideoResponse(Resource resource, String contentType, String fileName,
        Long fileLength) {
     return ResponseEntity.ok()
     .contentType(MediaType.parseMediaType(contentType))
     .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
     .header(HttpHeaders.ACCEPT_RANGES, "bytes")
     .header(HttpHeaders.CONTENT_LENGTH,String.valueOf(fileLength))
     .body(resource);
    }
    private ResponseEntity<Resource> buildPartialVideoResponse(Path filePath, String rangeHeader, String contentType,
        String fileName, Long fileLength) throws IOException {
      Long[]range=FileHandlerUtil.parseRangeHeader(rangeHeader, fileLength);
      Long rangeStart=range[0];
      Long rangeEnd=range[1];

      if (!isValidRange(rangeStart,rangeEnd,fileLength)) {
        return buildRangeNotSatisfiableResponse(fileLength);
      }
      Long contentLength=rangeEnd - rangeStart + 1;
      Resource rangeResource=FileHandlerUtil.createRangeResource(filePath, rangeStart, contentLength);
      return ResponseEntity.status(206)
      .contentType(MediaType.parseMediaType(contentType))
      .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
      .header(HttpHeaders.ACCEPT_RANGES, "bytes")
      .header(HttpHeaders.CONTENT_RANGE, "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
      .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength)) 
      .body(rangeResource);
    }
    private boolean isValidRange(Long rangeStart, Long rangeEnd, Long fileLength) {
      return rangeStart<=rangeEnd && rangeStart >=0 && rangeEnd < fileLength;
    }
    private ResponseEntity<Resource> buildRangeNotSatisfiableResponse(Long fileLength) {
     return ResponseEntity.status(416)
     .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength)
     .build();
    }
    @Override
    public ResponseEntity<Resource> serveImage(String uuid) {
      try{
        Path filePath=FileHandlerUtil.findFileByUuid(imageStorageLocation, uuid);
        Resource resource=FileHandlerUtil.createFullResource(filePath);
        String filename=resource.getFilename();
        String contentType=FileHandlerUtil.detectImageContentType(filename);
        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
        .body(resource);
      }
      catch(Exception ex){
        return ResponseEntity.notFound().build();
      }
    }
  }





