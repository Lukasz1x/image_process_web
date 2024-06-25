package org.example.web;

public class Image
{
    private String imageBase64;
    private int brightness;

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String image) {
        this.imageBase64 = image;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

}
