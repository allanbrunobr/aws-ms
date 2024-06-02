package com.br.multicloudecore.awsmodule.service;

import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.BoundingBoxDTO;
import com.br.multicloudecore.awsmodule.model.dto.rekognition.output.FaceInfoWithPositionDTO;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ImageProcessingService {

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

                    g2d.setStroke(new BasicStroke(5));
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
