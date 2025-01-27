package org.example.image;

import java.awt.image.BufferedImage;

public class WorkerBrightness implements Runnable{
    public int start;
    public int end;
    public BufferedImage image;
    public int value;

    public WorkerBrightness(int start, int end, BufferedImage image, int value) {
        this.start = start;
        this.end = end;
        this.image = image;
        this.value = value;
    }

    @Override
    public void run() {
        int width = image.getWidth();
        int blue;
        int green;
        int red;
        for(int y = start; y < end; y++){
            for(int x = 0; x < width; x++){
                int rgb = image.getRGB(x,y);
                blue = rgb & 0xff;
                green = (rgb >> 8) & 0xff;
                red = (rgb >> 16) & 0xff;

                red = Clamp.clamp(red+value,0,255);
                blue = Clamp.clamp(blue+value,0,255);
                green = Clamp.clamp(green+value,0,255);
                rgb = (0xff<<24) | (red<<16) | (green<<8) | blue;
                image.setRGB(x,y,rgb);
            }
        }

    }
}
