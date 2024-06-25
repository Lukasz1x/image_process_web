package org.example.web;

import org.example.image.ImageProcessing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class ImageFormController {

    private static String UPLOADED_FOLDER = "src/main/resources/static/images/";

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/imageformupload")
    public String upload(@RequestParam("image") MultipartFile file, @RequestParam("brightness") int brightness, RedirectAttributes redirectAttributes, Model model) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }

        try {
            byte[] bytes = file.getBytes();
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));

            ImageProcessing imageProcessing = new ImageProcessing();
            imageProcessing.setImage(bufferedImage);
            imageProcessing.increaseBrightnessMulti(brightness);

            String base64Image = imageProcessing.processToBase64();

            model.addAttribute("image", base64Image);
            model.addAttribute("red", ImageProcessing.processToBase64(imageProcessing.generateHistogramImage("red")));
            model.addAttribute("green", ImageProcessing.processToBase64(imageProcessing.generateHistogramImage("green")));
            model.addAttribute("blue", ImageProcessing.processToBase64(imageProcessing.generateHistogramImage("blue")));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "image";
    }
}
