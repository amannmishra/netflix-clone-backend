package com.netflix.clone.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.netflix.clone.service.FileUploadService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/files")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/upload/video")
    public ResponseEntity<Map<String,String>> uploadVideo(@RequestParam("file")MultipartFile file){
        String uuid=fileUploadService.storeVideoFile(file);
        return ResponseEntity.ok(buildUploadResponse(uuid, file));
    }

    private Map<String,String> buildUploadResponse(String uuid, MultipartFile file) {
    Map<String,String> response = new HashMap<>();
    response.put("uuid", uuid);
    response.put("filename", file.getOriginalFilename());
    response.put("size", String.valueOf(file.getSize()));
    return response;
}
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String,String>> uploadImage(@RequestParam("file")MultipartFile file){
        String uuid=fileUploadService.storeImageFile(file);
        return ResponseEntity.ok(buildUploadResponse(uuid, file));
    }
    @GetMapping("/video/{uuid}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String uuid,
        @RequestHeader(value = "Range",required = false)String rangeHeader,
        @RequestHeader(value = "token",required = false)String tokenParam){
            return fileUploadService.serveVideo(uuid,rangeHeader);
        }
    @GetMapping("/image/{uuid}")
    public ResponseEntity<Resource>serveImage(@PathVariable String uuid){
        return fileUploadService.serveImage(uuid);
    }

}
