package com.br.multicloudecore.awsmodule.controllers;

import com.br.multicloudecore.awsmodule.service.AWSRekognitionService;
import com.br.multicloudecore.awsmodule.service.AWSS3Service;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


/**
 * This class defines the controller for the AWS module, handling requests related to AWS services.
 */
@Controller
public class AwsController {


  private final AWSRekognitionService awsRekognitionService;
  private final AWSS3Service awss3Service;

  /**
   * Constructor for the AwsController class.
   *
   * @param awsRekognitionService an instance of AWSRekognitionService used
   *                              to interact with AWS Rekognition
   * @param awss3Service          an instance of AWSS3Service used to interact with AWS S3
   */
  public AwsController(AWSRekognitionService awsRekognitionService, AWSS3Service awss3Service) {
    this.awsRekognitionService = awsRekognitionService;
    this.awss3Service = awss3Service;
  }

  /**
   * Redirects the user to the login page of the frontend application.
   *
   * @return a redirect to the login page
   */
  @GetMapping("/")
    public String loginAws() {

    return "redirect:http://localhost:3000";
  }

  /**
   * Redirects the user to the logout page of the frontend application.
   *
   * @return a redirect to the logout page
   */
  @GetMapping("/logout")
  public String logoutAws() {
    return "redirect:http://localhost:3000";
  }

  /**
   * Redirects the user to the main page of the frontend application.
   *
   * @return a redirect to the main page
   */
  @GetMapping("/index-aws")
    public String indexAws() {
    return "indexAWS";
  }

  /**
   * Renders the page for the Rekognition service.
   *
   * @return the page for the Rekognition service
   */
  @GetMapping("/rekognition")
    public ModelAndView rekognitionAws() {
    return new ModelAndView("ai/rekognition/rekognition");
  }

  /**
   * Processes an uploaded image using Rekognition to detect faces and age,
   * and displays the results.
   *
   * @param file the uploaded image file
   * @return a ModelAndView object containing the results of the Rekognition analysis
   * @throws IOException if an error occurs while processing the image
   */
  @PostMapping("/rekognitionFace")
    public ModelAndView rekognitionFace(@RequestParam("file") MultipartFile file)
                                                throws IOException {
    awss3Service.storeFile(file);
    byte[] imageBytes = file.getBytes();

    String base64EncodedImage = awsRekognitionService
                .processImage(imageBytes, awsRekognitionService.detectFacesAndAgeRekognition(file));

    ModelAndView modelAndView = new ModelAndView("ai/rekognition/rekognition-result");
    modelAndView.addObject("faceInfoList",
                awsRekognitionService.detectFacesAndAgeRekognition(file));
    modelAndView.addObject("base64EncodedImage", base64EncodedImage);

    return modelAndView;
  }

  /**
   * Uploads a captured photo to AWS S3.
   *
   * @param imageData the captured photo data
   * @return a redirect to the main page of the AWS module
   */@PostMapping("/uploadPhoto")
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

  /**
   * Renders the page for capturing a photo.
   *
   * @return the page for capturing a photo
   */
  @GetMapping("/capturePhoto")
    public String capturePhoto() {
    return "photo/capturePhoto";
  }
}
