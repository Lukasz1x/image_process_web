package org.example.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
public class ImageController
{
    @PostMapping("/increasebrightness")
    public ResponseEntity<String> increaseBrightness(@RequestBody Image image)
    {
        String imageString="";
        String base64 = image.getImageBase64();
        int brightness = image.getBrightness();
        String imageCode;
        String imageType;
        String[] a = base64.split(",",2);
        imageType = a[0];
        imageCode = a[1];
        imageType = imageType.substring(imageType.indexOf("/")+1, imageType.indexOf(";"));
        byte[] decodedBytes = Base64.getDecoder().decode(imageCode);
        try {
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            changeBrightness(bufferedImage, brightness);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,imageType,byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            byteArrayOutputStream.close();
            imageString=a[0]+","+encoder.encodeToString(imageBytes);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(imageString, HttpStatus.OK);
    }

    @PostMapping("/increaseBrightnessBytes")
    public ResponseEntity<byte[]> increaseBrightnessBytes (@RequestBody Image image) {
        String base64 = image.getImageBase64();
        int brightness = image.getBrightness();
        String imageCode;
        String imageType;
        String[] a = base64.split(",", 2);
        imageType = a[0];
        imageCode = a[1];
        imageType = imageType.substring(imageType.indexOf("/") + 1, imageType.indexOf(";"));

        byte[] imageBytes = new byte[0];
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(imageCode);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(decodedBytes));
            changeBrightness(bufferedImage, brightness);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, imageType, byteArrayOutputStream);
            imageBytes = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/" + imageType);
            headers.add("Content-Length", String.valueOf(imageBytes.length));
            return new ResponseEntity<>(imageBytes,headers, HttpStatus.OK);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return new ResponseEntity<>(imageBytes, HttpStatus.BAD_REQUEST);
    }

    private void changeBrightness(BufferedImage originalImage, int brightness) {

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                Color color = new Color(originalImage.getRGB(x, y));
                int r = clamp(color.getRed() + brightness);
                int g = clamp(color.getGreen() + brightness);
                int b = clamp(color.getBlue() + brightness);
                Color newColor = new Color(r, g, b);
                originalImage.setRGB(x, y, newColor.getRGB());
            }
        }
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(value, 255));
    }
}
