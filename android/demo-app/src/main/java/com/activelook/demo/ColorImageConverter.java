package com.activelook.demo;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.activelook.activelooksdk.types.ImageConverter;
import com.activelook.activelooksdk.types.ImageData;
import com.activelook.activelooksdk.types.ImageMDP05;

public class ColorImageConverter {

    private static final byte[] RED_LEVELS = {
            (byte) 0x00, (byte) 0x04, (byte) 0x20, (byte) 0x14, (byte) 0x18,
            (byte) 0x28, (byte) 0x1C, (byte) 0x38, (byte) 0x3C
    };
    private static final byte[] GREEN_LEVELS = {
            (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x41, (byte) 0x81,
            (byte) 0x82, (byte) 0x43, (byte) 0x83, (byte) 0xC3
    };

    public static ImageData convert(Bitmap img) {
        img = ImageMDP05.rotateBMP_180(img); // rotation générique déjà publique dans le SDK
        int height = img.getHeight(), width = img.getWidth();
        int[] pixels = new int[width * height];
        img.getPixels(pixels, 0, width, 0, 0, width, height);

        byte[] raw = new byte[width * height];
        for (int i = 0; i < pixels.length; i++) {
            int pxl = pixels[i];
            int rIdx = Math.round(Color.red(pxl) * 8f / 255f);
            int gIdx = Math.round(Color.green(pxl) * 8f / 255f);
            raw[i] = (byte) ((RED_LEVELS[rIdx] & 0xFF) | (GREEN_LEVELS[gIdx] & 0xFF));
        }

        byte[] compressed = ImageConverter.getCmdCompress4BppHeatshrink(raw);
        return new ImageData(width, compressed, raw.length);
    }
}
