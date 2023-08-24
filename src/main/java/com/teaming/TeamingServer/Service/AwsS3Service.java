package com.teaming.TeamingServer.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.teaming.TeamingServer.Domain.entity.Member;
import com.teaming.TeamingServer.Exception.BaseException;
import com.teaming.TeamingServer.Repository.MemberRepository;
import com.teaming.TeamingServer.common.BaseErrorResponse;
import com.teaming.TeamingServer.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
                    .body(new BaseResponse<String>(
                            "프로필 변경이 완료되었습니다.", fileUrl));

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
}
