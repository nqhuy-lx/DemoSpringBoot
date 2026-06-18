package com.hnq.controller;

import com.hnq.dto.response.ResponseData;
import com.hnq.dto.response.ResponseError;
import com.hnq.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private final MailService mailService;

    @PostMapping("/send-email")
    public ResponseData<String> sendEmail(@RequestParam String subject,
                                          @RequestParam String text,
                                          @RequestParam String to,
                                          @RequestParam(required = false) MultipartFile[] files) {
        try{
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), mailService.sendEmail(to, subject, text, files));
        } catch (Exception e){
            log.error("send email error {}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "send email error");
        }
    }
}
