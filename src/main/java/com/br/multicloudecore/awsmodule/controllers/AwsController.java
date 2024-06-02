package com.br.multicloudecore.awsmodule.controllers;

import com.br.multicloudecore.awsmodule.service.AwsRekognitionService;
import com.br.multicloudecore.awsmodule.service.AwsS3Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import com.br.multicloudecore.awsmodule.service.ImageProcessingService;
import com.br.multicloudecore.util.ConstantsAWSMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import static com.br.multicloudecore.util.ConstantsAWSMS.ERROR_MESSAGE;


/**
 * This class defines the controller for the AWS module, handling requests related to AWS services.
 */
@Controller
public class AwsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsController.class);
  private final AwsRekognitionService awsRekognitionService;
  private final AwsS3Service awss3Service;
  private final ImageProcessingService imageProcessingService;

    /**
     * Constructor for the AwsController class.
     *
     * @param awsRekognitionService an instance of AWSRekognitionService used
     *                              to interact with AWS Rekognition
     * @param awss3Service          an instance of AWSS3Service used to interact with AWS S3
     */
    public AwsController(AwsRekognitionService awsRekognitionService, AwsS3Service awss3Service, ImageProcessingService imageProcessingService) {
        this.awsRekognitionService = awsRekognitionService;
        this.awss3Service = awss3Service;
        this.imageProcessingService = imageProcessingService;
    }

    /**
     * Redirects the user to the login page of the frontend application.
     *
     * @return a redirect to the login page
     */
    @GetMapping("/")
    public String loginAws() {

        return "redirect:http://localhost:3001";
    }

    /**
     * Redirects the user to the logout page of the frontend application.
     *
     * @return a redirect to the logout page
     */
    @GetMapping("/logout")
    public String logoutAws() {
        return "redirect:http://localhost:3001q";
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
      ModelAndView modelAndView;
        try {
            awss3Service.storeFile(file);
            byte[] imageBytes = file.getBytes();

            String base64EncodedImage = imageProcessingService
                    .processImage(imageBytes, awsRekognitionService.detectFacesAndAgeRekognition(file));

            modelAndView = new ModelAndView("ai/rekognition/rekognition-result");
            modelAndView.addObject("faceInfoList",
                    awsRekognitionService.detectFacesAndAgeRekognition(file));
            modelAndView.addObject("base64EncodedImage", base64EncodedImage);
        } catch (IOException e) {
          LOGGER.error("Failed to process the image", e);
          modelAndView = new ModelAndView(ConstantsAWSMS.ERROR);
          modelAndView.addObject(ERROR_MESSAGE, "Falha ao processar a imagem. Por favor, tente novamente.");
        } catch (Exception e) {
          LOGGER.error("Unexpected error while processing the image", e);
          modelAndView = new ModelAndView(ConstantsAWSMS.ERROR);
          modelAndView.addObject(ERROR_MESSAGE, "Ocorreu um erro inesperado. Por favor, tente novamente.");
        }
            return modelAndView;
        }

        /**
         * Uploads a captured photo to AWS S3.
         *
         * @param imageData the captured photo data
         * @return a redirect to the main page of the AWS module
         */@PostMapping("/uploadPhoto")
        public String uploadPhoto ( @RequestBody byte[] imageData, Model model){
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                BufferedImage image = ImageIO.read(inputStream);
                awss3Service.uploadPhoto(image, "captured_image.png");

                return "indexAWS";
            } catch (IOException e) {
                LOGGER.error("Failed to upload photo", e);
                model.addAttribute("errorMessage", "Falha ao enviar a foto. Por favor, tente novamente.");
                return "error";
            }
        }

        /**
         * Renders the page for capturing a photo.
         *
         * @return the page for capturing a photo
         */
        @GetMapping("/capturePhoto")
        public String capturePhoto () {
            return "photo/capturePhoto";
        }

        @GetMapping("/error")
        public String errorPage (Model model){
            model.addAttribute("errorMessage", "Um erro ocorreu.");
            return "error";
        }
    }

