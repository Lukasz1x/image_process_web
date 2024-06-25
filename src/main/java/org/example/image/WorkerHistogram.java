package org.example.image;

import java.awt.image.BufferedImage;
import java.util.Map;

public class WorkerHistogram implements Runnable {
    private final int start;
    private final int end;
    private final BufferedImage image;
    private final String channel;
    private final Map<Integer, Integer> histogram;

    public WorkerHistogram(int start, int end, BufferedImage image, String channel, Map<Integer, Integer> histogram) {
        this.start = start;
        this.end = end;
        this.image = image;
        this.channel = channel;
        this.histogram = histogram;
    }

    @Override
    public void run() {
        int width = image.getWidth();
        for (int y = start; y < end; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int value = 0;

                switch (channel.toLowerCase()) {
                    case "red":
                        value = (rgb >> 16) & 0xff;
                        break;
                    case "green":
                        value = (rgb >> 8) & 0xff;
                        break;
                    case "blue":
                        value = rgb & 0xff;
                        break;
                }

                histogram.compute(value, (k, v) -> v == null ? 1 : v + 1);
            }
        }
    }
}


