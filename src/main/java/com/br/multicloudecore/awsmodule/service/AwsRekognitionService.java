package com.br.multicloudecore.awsmodule.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.util.IOUtils;
import com.br.multicloudecore.awsmodule.component.DynamicAWSCredentialsProvider;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.mapper.FaceDetailMapper;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.FaceInfoWithPositionDTO;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.result.FaceDetailDTO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * The AWSRekognitionService class provides functionality for detecting
 * faces in images using the AWS Rekognition service.
 * It uses the AWS SDK to interact with the Rekognition API and returns
 * a list of {@link FaceInfoWithPositionDTO} objects
 * containing the detected face details and bounding box information.
 */
@Service
public class AwsRekognitionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsRekognitionService.class);
  private final DynamicAWSCredentialsProvider dynamicAwsCredentialsProvider;
  private AmazonRekognition rekognitionClient;

  @Autowired
  public AwsRekognitionService(DynamicAWSCredentialsProvider dynamicAwsCredentialsProvider) {
    this.dynamicAwsCredentialsProvider = dynamicAwsCredentialsProvider;
    this.rekognitionClient = AmazonRekognitionClientBuilder.standard().build();
    waitForVaultCredentials();
  }

  /**
    * Detects faces in the provided image using AWS Rekognition
    * and returns a list of {@link FaceInfoWithPositionDTO} objects
    * containing the detected face details and bounding box information.
    *
    * @param file The image file to be analyzed.
    * @return A list of {@link FaceInfoWithPositionDTO} objects
    * containing the detected face details and bounding box information.
    */
  public List<FaceInfoWithPositionDTO> detectFacesAndAgeRekognition(MultipartFile file) {
    List<FaceInfoWithPositionDTO> faceInfoList = new ArrayList<>();

    try (InputStream inputStream = file.getInputStream()) {
      byte[] imageBytes = IOUtils.toByteArray(inputStream);
      ByteBuffer imageByteBuffer = ByteBuffer.wrap(imageBytes);

      DetectFacesRequest facesRequest = new DetectFacesRequest()
               .withImage(new Image().withBytes(imageByteBuffer)).withAttributes(Attribute.ALL);

      DetectFacesResult facesResult = this.rekognitionClient.detectFaces(facesRequest);
      FaceDetailMapper faceDetailMapper = new FaceDetailMapper();

      for (FaceDetail face : facesResult.getFaceDetails()) {
        if (facesRequest.getAttributes().contains("ALL")) {
          FaceDetailDTO faceDetailDTO = faceDetailMapper.map(face);
          BoundingBox boundingBox = face.getBoundingBox();
          FaceInfoWithPositionDTO faceInfo =
                  new FaceInfoWithPositionDTO(faceDetailDTO, boundingBox);
          faceInfoList.add(faceInfo);
        }
      }
    } catch (AmazonRekognitionException e) {
      LOGGER.error("Error occurred during face detection with Rekognition", e);
    } catch (IOException e) {
      LOGGER.error("Error occurred while reading image file", e);
    }
    return faceInfoList;
  }

 private void waitForVaultCredentials() {
        new Thread(() -> {
            boolean credentialsAvailable = false;
            while (!credentialsAvailable) {
                try {
                    this.rekognitionClient = AmazonRekognitionClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(
                                    dynamicAwsCredentialsProvider.getCredentials()))
                            .build();
                    credentialsAvailable = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(1000); // Wait for 1 second before retrying
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }
}
