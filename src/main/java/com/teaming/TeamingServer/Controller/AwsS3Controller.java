//package com.teaming.TeamingServer.Controller;
//
//import com.teaming.TeamingServer.Domain.entity.AwsS3;
//import com.teaming.TeamingServer.Service.AwsS3Service;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//public class AwsS3Controller {
//
//    private final AwsS3Service awsS3Service;
//
//    @PatchMapping("member/{memberId}/mypage/change-image")
//    public AwsS3 upload(@RequestPart("file") MultipartFile multipartFile) throws IOException {
//        return awsS3Service.upload(multipartFile, "upload");
//    }
//
//    @DeleteMapping("/resource")
//    public void remove(AwsS3 awsS3) {
//        awsS3Service.remove(awsS3);
//    }
//}
