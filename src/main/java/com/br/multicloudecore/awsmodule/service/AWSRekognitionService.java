package com.br.multicloudecore.awsmodule.service;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.util.IOUtils;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.mapper.FaceDetailMapper;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.BoundingBoxDTO;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.FaceInfoWithPositionDTO;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.result.FaceDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AWSRekognitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3Service.class);

    private final AWSCredentialsProvider awsCredentialsProvider;


    @Autowired
    public AWSRekognitionService(AWSCredentialsProvider awsCredentialsProvider) {
        this.awsCredentialsProvider = awsCredentialsProvider;
    }

    public List<FaceInfoWithPositionDTO> detectFacesAndAgeRekognition(MultipartFile file) {
        List<FaceInfoWithPositionDTO> faceInfoList = new ArrayList<>();
           // Criar o caminho para a pasta de recursos
            try (InputStream inputStream = file.getInputStream()) {
                byte[] imageBytes = IOUtils.toByteArray(inputStream);
                ByteBuffer imageByteBuffer = ByteBuffer.wrap(imageBytes);

                // continuar com a análise da imagem como antes
            AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard()
                    .withCredentials(awsCredentialsProvider)
                    .build();

            DetectFacesRequest facesRequest = new DetectFacesRequest()
                    .withImage(new Image().withBytes(imageByteBuffer)).withAttributes(Attribute.ALL);

            DetectFacesResult facesResult = rekognitionClient.detectFaces(facesRequest);
            FaceDetailMapper faceDetailMapper = new FaceDetailMapper();

            for (FaceDetail face : facesResult.getFaceDetails()) {
                if (facesRequest.getAttributes().contains("ALL")) {
                    FaceDetailDTO faceDetailDTO = faceDetailMapper.map(face);
                    BoundingBox boundingBox = face.getBoundingBox();
                    FaceInfoWithPositionDTO faceInfo = new FaceInfoWithPositionDTO(faceDetailDTO, boundingBox);
                    faceInfoList.add(faceInfo);
                }
            }
        } catch (AmazonRekognitionException | IOException e) {
            LOGGER.error("Error occurred during face detection with Rekognition", e);
        }

        return faceInfoList;

    }
    public String processImage(byte[] imageBytes, List<FaceInfoWithPositionDTO> faceInfoWithPositionDTOS) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(image, 0, 0, null);

                for (int i = 0; i < faceInfoWithPositionDTOS.size(); i++) {
                    FaceInfoWithPositionDTO faceDetail = faceInfoWithPositionDTOS.get(i);
                    BoundingBoxDTO box = faceDetail.getFaceDetailDTO().getBoundingBox();
                    int left = (int) (box.getLeft() * image.getWidth());
                    int top = (int) (box.getTop() * image.getHeight());
                    int width = (int) (box.getWidth() * image.getWidth());
                    int height = (int) (box.getHeight() * image.getHeight());

                    int red = ((i + 1) * 191) % 256;
                    int green = ((i + 1) * 23) % 256;
                    int blue = ((i + 1) * 77) % 256;
                    Color faceColor = new Color(red, green, blue);

                    g2d.setStroke(new BasicStroke(5)); // Set the line width to 2 pixels
                    g2d.setColor(faceColor);
                    g2d.drawRect(left, top, width, height);

                    g2d.setFont(new Font("Arial", Font.BOLD, 30));
                    int textHeight = g2d.getFontMetrics().getHeight();
                    int textWidth = g2d.getFontMetrics().stringWidth(String.valueOf(i + 1));

                    g2d.drawString(String.valueOf(i + 1), left + (width / 2) - (textWidth / 2), top + (height / 2) + (textHeight / 4));


            }
            }
        };
        panel.setSize(image.getWidth(), image.getHeight());

        BufferedImage resultImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resultImage.createGraphics();
        panel.paint(g2d);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resultImage, "jpg", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
