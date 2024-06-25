package org.example.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.*;

public class ImageProcessing
{
    public BufferedImage image;

    public void read(String path) throws IOException {
        image = ImageIO.read(new File(path));
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void write(String path) throws IOException {
        String format;
        if(path.lastIndexOf('.')!=-1)
        {
            format = path.substring(path.lastIndexOf('.')+1);
        }else {
            format="jpg";
            path+=".jpg";
        }

        ImageIO.write(image, format, new File(path));
    }

    public static void write(BufferedImage bufferedImage, String path) throws IOException {
        String format;
        if(path.lastIndexOf('.')!=-1)
        {
            format = path.substring(path.lastIndexOf('.')+1);
        }else {
            format="jpg";
            path+=".jpg";
        }

        ImageIO.write(bufferedImage, format, new File(path));
    }

    public String processToBase64()
    {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            byteArrayOutputStream.close();
            String imageString = encoder.encodeToString(imageBytes);
            return imageString;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String processToBase64(BufferedImage bufferedImage)
    {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            byteArrayOutputStream.close();
            String imageString = encoder.encodeToString(imageBytes);
            return imageString;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void increaseBrightness(int value)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int red, green, blue;
        for(int y=0; y<height; y++)
        {
            for(int x=0; x<width; x++)
            {
                int rgb = image.getRGB(x,y);
                blue = rgb & 255;
                green = (rgb>>8) & 255;
                red = (rgb>>16) & 255;
                blue = Clamp.clamp(blue + value, 0, 255);
                green = Clamp.clamp(green + value, 0, 255);
                red = Clamp.clamp(red + value, 0, 255);
                rgb=(255<<8);
                rgb = (rgb | red)<<8;
                rgb = (rgb | green)<<8;
                rgb = rgb | blue;


                image.setRGB(x,y,rgb);
                //System.out.println("RGB signed 32 bit = "+rgb+" hex: "+Integer.toHexString(rgb)+ " blue " + blue);

            }
        }
    }

    public void increaseBrightnessMulti(int value) {
        int width = image.getWidth();
        int height = image.getHeight();
        int cores = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[cores];
        int chunkSize = height / cores;
        //System.out.println("Cores: "+ cores);
        for (int i = 0; i < cores; i++) {
            int start = i * chunkSize;
            int endIndex = (i == cores - 1) ? height : (i + 1) * chunkSize;
            threads[i] = new Thread(new WorkerBrightness(start, endIndex, image, value));
            threads[i].start();
        }


        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int[] calculateHistogram(String channel) {
        int width = image.getWidth();
        int height = image.getHeight();
        int cores = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[cores];
        int chunkSize = height / cores;
        Map<Integer, Integer> histogram = new ConcurrentHashMap<>();

        for (int i = 0; i < 256; i++) {
            histogram.put(i, 0);
        }

        for (int i = 0; i < cores; i++) {
            int start = i * chunkSize;
            int endIndex = (i == cores - 1) ? height : (i + 1) * chunkSize;
            threads[i] = new Thread(new WorkerHistogram(start, endIndex, image, channel, histogram));
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int[] resultHistogram = new int[256];
        for (Map.Entry<Integer, Integer> entry : histogram.entrySet()) {
            resultHistogram[entry.getKey()] = entry.getValue();
        }

        return resultHistogram;
    }

    public BufferedImage generateHistogramImage(String color)
    {
        Color c;
        switch (color) {
            case "red" -> c = Color.RED;
            case "green" -> c = Color.GREEN;
            case "blue" -> c = Color.BLUE;
            default -> {
                return null;
            }
        }
        int[] histogram= this.calculateHistogram(color);
        int maxValue = Integer.MIN_VALUE;

        for (int value : histogram) {
            if (value > maxValue) {
                maxValue = value;
            }
        }

        BufferedImage histogramImage = new BufferedImage(256, 128, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = histogramImage.createGraphics();
        graphics2D.setColor(Color.BLACK);
        graphics2D.fillRect(0,0,256,128);
        graphics2D.setColor(c);

        for(int i =0; i< histogram.length; i++)
        {
            graphics2D.fillRect(i, 128 - ((128*histogram[i])/maxValue), 1,256);
        }
        graphics2D.dispose();
        return histogramImage;
    }
}
