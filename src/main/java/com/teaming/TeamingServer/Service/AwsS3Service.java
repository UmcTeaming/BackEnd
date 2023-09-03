package com.teaming.TeamingServer.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.teaming.TeamingServer.Domain.entity.File;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Domain.entity.Project;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.Repository.ProjectRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import com.teaming.TeamingServer.common.KeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public ResponseEntity profileImageUpload(MultipartFile multipartFile, String key, Long memberId) throws IOException {
        try {
            Member member = memberRepository.findById(memberId).get();

            // https://teamingbucket.s3.ap-northeast-2.amazonaws.com/image/문상훈짤.jpeg
            // 원래 있던 프로필 파일 S3 에서 삭제
            // (1) https: | empty | teamingbucket.s3.ap-northeast-2.amazonaws.com | image | 파일 이름
            if(member.getProfile_image() != null) {
                deleteFile(member.getProfile_image());
            }

            // 파일 이름 받기
            String fileName = "memberId-" + memberId + multipartFile.getOriginalFilename();

            // 파일 메타데이터 빼서, S3 에 저장할 수 있도록 세팅하기
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // S3 에 업로드
            amazonS3Client.putObject(bucket,key + fileName , multipartFile.getInputStream(), metadata);

            // S3 에 업로드한 파일 링크 생성하기
            String fileUrl = generateS3Link(bucket, key + fileName);

            // 업로드 된 프로필 이미지 Member DB 에 반영하기
            // member 프로필 이미지 변경
            member.updateProfileImage(fileUrl);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new BaseResponse<String>(HttpStatus.OK.value()
                                                , "프로필 변경이 완료되었습니다.", fileUrl));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @Transactional
    public String projectImageUpload(MultipartFile multipartFile, String key, String projectName) {
        try {

            // 파일 이름 받기
            String fileName = "projectName-" + projectName + multipartFile.getOriginalFilename();

            // 파일 메타데이터 빼서, S3 에 저장할 수 있도록 세팅하기
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // S3 에 업로드
            amazonS3Client.putObject(bucket,key + fileName , multipartFile.getInputStream(), metadata);

            // S3 에 업로드한 파일 링크 생성하기
            String fileUrl = generateS3Link(bucket, key + fileName);

            return fileUrl;

        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public String[] projectFileUpload(MultipartFile multipartFile, String key, Long projectID) {
        try {

            // 파일 이름 받기 : 파일 이름이 중복될 수 있으니, 랜덤 숫자 추가
            String fileName = projectID + "-" + KeyGenerator.createKey() + multipartFile.getOriginalFilename();

            // 파일 메타데이터 빼서, S3 에 저장할 수 있도록 세팅하기
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // S3 에 업로드
            amazonS3Client.putObject(bucket,key + fileName , multipartFile.getInputStream(), metadata);

            // S3 에 업로드한 파일 링크 생성하기
            String[] fileUrl_storedFileName = {generateS3Link(bucket, key + fileName), fileName};

            return fileUrl_storedFileName;

        } catch (Exception e) {
            throw new BaseException(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Transactional
    public byte[] download(File file) {

        S3Object s3Object = amazonS3Client.getObject(bucket, "file/" + file.getStoredFileName());
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            throw new BaseException(HttpStatus.NO_CONTENT.value(), e.getMessage());
        }
    }

    @Transactional
    // 원래 있던 파일 삭제
    public void deleteFile(String imageLink) {

        try {
            String[] fileLink = imageLink.split("/");
            String beforeFileKey = fileLink[3] + "/" + fileLink[4];
            amazonS3Client.deleteObject(bucket, beforeFileKey);
        } catch (Exception e) {
            log.debug("Delete File failed", e);
            throw new BaseException(HttpStatus.NO_CONTENT.value(), "Delete File failed");
        }
    }

    private String generateS3Link(String bucket, String key) {
        //https://teamingbucket.s3.ap-northeast-2.amazonaws.com
        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;
    }

    // 프로젝트 파일 삭제하기
    @Transactional
    public void deleteProjectFiles(List<File> filesToDelete){
        try{
            for(File file : filesToDelete)
            {
                amazonS3Client.deleteObject(bucket, "file/" + file.getStoredFileName());
            }
        } catch (Exception e){
            log.debug("Delete File failed", e);
            throw new BaseException(HttpStatus.NO_CONTENT.value(), "Delete File failed");
        }
    }
}
