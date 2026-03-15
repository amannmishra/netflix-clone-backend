package com.netflix.clone.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;

public class FileHandlerUtil {

    private FileHandlerUtil() {
    }

    // file ka extension pta kr rhe hai
    public static String extractFileExtension(String originalFileName) {
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return fileExtension;
    }

    // file ko find kr rhe hai by uuid
    public static Path findFileByUuid(Path directory, String uuid) throws Exception {
        return Files.list(directory)
                .filter(path -> path.getFileName().toString().startsWith(uuid))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found for UUID: " + uuid));
    }

    // video content type
    public static String detectVideoContentType(String filename) {
        if (filename == null) return "video/mp4";
        if (filename.endsWith(".webm")) return "video/webm";
        if (filename.endsWith(".ogg")) return "video/ogg";
        if (filename.endsWith(".mkv")) return "video/mkv";
        if (filename.endsWith(".avi")) return "video/avi";
        if (filename.endsWith(".flv")) return "video/flv";
        if (filename.endsWith(".wmv")) return "video/wmv";
        if (filename.endsWith(".m4v")) return "video/m4v";
        if (filename.endsWith(".3gp")) return "video/3gp";
        if (filename.endsWith(".mpg")) return "video/mpg";
        if (filename.endsWith(".mpeg")) return "video/mpeg";

        return "video/mp4";
    }

    // image ka content type detect
    public static String detectImageContentType(String fileName) {
        if (fileName == null) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    // file ki length dekh rhe hai aur bytes me convert kr rhe hai
    public static Long[] parseRangeHeader(String rangeHeader, Long fileLength) {
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        Long rangeStart = Long.parseLong(ranges[0]);
        Long rangeEnd = (ranges.length > 1 && !ranges[1].isEmpty())
                ? Long.parseLong(ranges[1])
                : fileLength - 1;

        return new Long[]{rangeStart, rangeEnd};
    }

    public static Resource createRangeResource(Path filePath, Long rangeStart, Long rangeLength) throws IOException {
        RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");
        fileReader.seek(rangeStart);

        InputStream partialContentStream = new InputStream() {
            private long totalBytesRead = 0;


            @Override
            public int read() throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                totalBytesRead++;
                return fileReader.read();
            }

            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                    return -1;
                }
                Long remainingBytes = rangeLength - totalBytesRead;
                int bytesToRead = (int) Math.min(length, remainingBytes);
                int bytesActuallyRead = fileReader.read(buffer, offset, bytesToRead);

                if (bytesActuallyRead > 0) {
                    totalBytesRead += bytesActuallyRead;
                }
                if (totalBytesRead >= rangeLength) {
                    fileReader.close();
                }
                return bytesActuallyRead;
            }

            @Override
            public void close() throws IOException {
                fileReader.close();
            }
        };

        return new InputStreamResource(partialContentStream) {
            @Override
            public long contentLength() {
                return rangeLength;
            }
        };
    }
    public static Resource createFullResource(Path filePath) throws IOException {
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("File not found or not readable: " + filePath);
        }
        return resource;
    }
}
