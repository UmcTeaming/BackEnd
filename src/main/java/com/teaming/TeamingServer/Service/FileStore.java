package com.teaming.TeamingServer.Service;

import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Repository.FileRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

//import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FileStore {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    public static String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }
    public List<File> storeFiles(List<MultipartFile> multipartFiles)
            throws IOException {
        List<File> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                String type = extractExt(multipartFile.getName());
                File file = File.builder()
                        .fileName(multipartFile.getName())
                        .file_status(false)
                        .file_type(type)
                        .fileUrl(getFullPath(multipartFile.getName())).build();
                storeFileResult.add(file);
            }
        }
        return storeFileResult;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(fileDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found: " + fileName);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFileById(Long fileId) {
        // fileId를 기반으로 데이터베이스에서 파일 정보를 가져오는 로직
        // 예를 들어, JPA를 사용하여 파일 정보를 조회할 수 있습니다.
        // 반환된 파일 객체에는 파일 이름, 파일 타입 등의 정보가 포함됩니다.
        // 해당 메서드는 데이터베이스 연동에 맞게 구현해야 합니다.
        // 아래는 가상의 메서드입니다.

        // 예시:
        return fileRepository.findById(fileId).orElse(null);
    }
//    public File storeFile(MultipartFile multipartFile) throws IOException
//    {
//        if (multipartFile.isEmpty()) {
//            return null;
//        }
//        String originalFilename = multipartFile.getOriginalFilename();
//        String storeFileName = createStoreFileName(originalFilename);
//        multipartFile.transferTo(new File(getFullPath(storeFileName)));
//        return new File(originalFilename, storeFileName);
//    }
//    private String createStoreFileName(String originalFilename) {
//        String ext = extractExt(originalFilename);
//        String uuid = UUID.randomUUID().toString();
//        return uuid + "." + ext;
//    }
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    } }
