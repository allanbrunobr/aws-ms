package com.br.multicloudecore.awsmodule.controller;

import com.br.multicloudecore.awsmodule.service.AWSRekognitionService;
import com.br.multicloudecore.awsmodule.service.AWSS3Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Controller
public class AWSController {


    private final AWSRekognitionService awsRekognitionService;
    private final AWSS3Service awss3Service;

    public AWSController(AWSRekognitionService awsRekognitionService, AWSS3Service awss3Service) {
        this.awsRekognitionService = awsRekognitionService;
        this.awss3Service = awss3Service;
    }

    @GetMapping("/")
    public String loginAWS() {

        return "redirect:http://localhost:3000";
    }

    @GetMapping("/logout")
    public String logoutAWS() {

        return "redirect:http://localhost:3000";
    }

    @GetMapping("/index-aws")
    public String indexAWS() {
        return "indexAWS";
    }

    @GetMapping("/rekognition")
    public ModelAndView rekognitionAWS() {
        return new ModelAndView("ai/rekognition/rekognition");
    }

    @PostMapping("/rekognitionFace")
    public ModelAndView rekognitionFace(@RequestParam("file") MultipartFile file) throws IOException {
        awss3Service.storeFile(file);
        byte[] imageBytes = file.getBytes(); // Get bytes from MultipartFile

        String base64EncodedImage = awsRekognitionService.processImage(imageBytes,awsRekognitionService.detectFacesAndAgeRekognition(file));

        ModelAndView modelAndView = new ModelAndView("ai/rekognition/rekognition-result");
        modelAndView.addObject("faceInfoList", awsRekognitionService.detectFacesAndAgeRekognition(file));
        modelAndView.addObject("base64EncodedImage", base64EncodedImage);

        return modelAndView;
    }


    @PostMapping("/uploadPhoto")
    public String uploadPhoto(@RequestBody byte[] imageData) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            BufferedImage image = ImageIO.read(inputStream);
            awss3Service.uploadPhoto(image, "captured_image.png");

            return "indexAWS";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    @GetMapping("/capturePhoto")
    public String capturePhoto() {
          return "photo/capturePhoto";

    }
}
