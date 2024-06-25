package org.example;

import org.example.image.ImageProcessing;
import org.springframework.boot.SpringApplication;

import java.io.IOException;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ImageProcessing image = new ImageProcessing();
        long start, end;
        try {
            image.read("thailand.jpg");
            start= System.currentTimeMillis();
            image.increaseBrightness(50);
            end= System.currentTimeMillis();
            System.out.println("Time: " + (end-start));
            image.write("thailand2");

            image.read("thailand.jpg");
            start= System.currentTimeMillis();
            image.increaseBrightnessMulti(50);
            end= System.currentTimeMillis();
            System.out.println("Time: " + (end-start));
            image.write("thailand3");
            //histogram
            image.read("thailand.jpg");
            ImageProcessing.write(image.generateHistogramImage("red"),"red");
            ImageProcessing.write(image.generateHistogramImage("green"),"green");
            ImageProcessing.write(image.generateHistogramImage("blue"),"blue");
        } catch (IOException e) {
            e.printStackTrace();
        }



        SpringApplication.run(Main.class, args);
    }
}