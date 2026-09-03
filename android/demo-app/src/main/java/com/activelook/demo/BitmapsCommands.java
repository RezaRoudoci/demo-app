package com.activelook.demo;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.ImageData;
import com.activelook.activelooksdk.types.ImgSaveFormat;
import com.activelook.activelooksdk.types.ImgStreamFormat;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class BitmapsCommands extends CommandsBase {

    private final byte[] imagesSlot = {(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x03,(byte)0x04,(byte)0x05,(byte)0x06};

    private final byte ALL = (byte)0xFF;

    @Override
    protected String getCommandGroup() {
        return "Bitmaps commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        List<Map.Entry<String, Consumer<Glasses>>> commands = new ArrayList<>();

        commands.add(item("clear() (Clears the glasses screen)", glasses -> glasses.clear()));
        commands.add(item("imgList (List all saved images)", glasses -> glasses.imgList(r -> {
            BitmapsCommands.this.snack(String.format("imgList: %s", Arrays.toString(r.toArray())));
        })));
        commands.add(item("imgSave  4bpp (Saves a preloaded image from the app)", glasses -> {
            try {
                Bitmap img1 = BitmapFactory.decodeStream(getAssets().open("tigre_304x256.png"));
                glasses.cfgWrite("DemoApp", 1, 42);
                glasses.imgSave((byte) imagesSlot[1], img1, ImgSaveFormat.MONO_4BPP);
                img1.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        commands.add(item("imgSave 4bpp heatshrink (Saves a preloaded image from the app)", glasses -> {
            try {
                Bitmap img2 = BitmapFactory.decodeStream(getAssets().open("zebre_304x248.png"));
                glasses.cfgWrite("DemoApp", 1, 42);
                glasses.imgSave((byte) imagesSlot[2], img2, ImgSaveFormat.MONO_4BPP_HEATSHRINK);
                img2.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        commands.add(item("imgSave 4bpp heatshrink save comp (Saves a preloaded image from the app)", glasses -> {
            try {
                Bitmap img2 = BitmapFactory.decodeStream(getAssets().open("zebre_304x248.png"));
                glasses.cfgWrite("DemoApp", 1, 42);
                glasses.imgSave((byte) imagesSlot[3], img2, ImgSaveFormat.MONO_4BPP_HEATSHRINK_SAVE_COMP);
                img2.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        commands.add(item("imgSave1bpp (Saves the glasses logo)", glasses -> {
            try {
                Bitmap img3 = BitmapFactory.decodeStream(getAssets().open("glasses_90x36.png"));
                glasses.cfgWrite("DemoApp", 1, 42);
                glasses.imgSave((byte) imagesSlot[4], img3, ImgSaveFormat.MONO_1BPP);
                img3.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        if (connectedGlasses.isColorCapable()) {
            commands.add(item("imgSaveColor (Saves a preloaded image from the app in color)", glasses -> {
                try {
                    Bitmap img1 = BitmapFactory.decodeStream(getAssets().open("recap_marketing.png"));
                    glasses.cfgWrite("DemoApp", 1, 42);
                    ImageData imgData = ColorImageConverter.convert(img1);
                    glasses.imgSave((byte) imagesSlot[5], imgData, ImgSaveFormat.RG_COLOR_8BPP);
                    img1.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        commands.add(item("imgDisplay 4bpp (Displays saved image)", glasses -> {
            glasses.clear();
            glasses.cfgSet("DemoApp");
            glasses.imgDisplay((byte) imagesSlot[1], (short) 0, (short) 0);
        }));
        commands.add(item("imgDisplay 4bpp heatshrink (Displays saved image)", glasses -> {
            glasses.clear();
            glasses.cfgSet("DemoApp");
            glasses.imgDisplay((byte) imagesSlot[2], (short) 0, (short) 0);
        }));
        commands.add(item("imgDisplay 4bpp heatshrink save comp (Displays saved image)", glasses -> {
            glasses.clear();
            glasses.cfgSet("DemoApp");
            glasses.imgDisplay((byte) imagesSlot[3], (short) 0, (short) 0);
        }));
        commands.add(item("imgDisplay 1bpp (Displays glasses logo)", glasses -> {
            glasses.clear();
            glasses.cfgSet("DemoApp");
            glasses.imgDisplay((byte) imagesSlot[4], (short) 77, (short) 112);
        }));

        if (connectedGlasses.isColorCapable()) {
            commands.add(item("imgDisplayColor (Displays the saved color image)", glasses -> {
                glasses.clear();
                glasses.cfgSet("DemoApp");
                glasses.imgDisplay((byte) imagesSlot[5], (short) 0, (short) 0);
            }));
        }

        commands.add(item("imgDelete (Deletes the image from imSave 4bpp", glasses -> {
            glasses.cfgWrite("DemoApp", 1, 42);
            glasses.imgDelete((byte) imagesSlot[1]);
        }));
        commands.add(item("imgDeleteAll (Deletes all saved images)", glasses -> {
            glasses.cfgWrite("DemoApp", 1, 42);
            glasses.imgDelete(ALL);
        }));
        commands.add(item("imgStream 4bpp (Displays image from imgSave without saving)", glasses -> {
            try {
                Bitmap img1 = BitmapFactory.decodeStream(getAssets().open("tigre_304x256.png"));
                glasses.cfgWrite("DemoApp", 1, 42);
                glasses.imgStream(img1, ImgStreamFormat.MONO_4BPP_HEATSHRINK, (short) 0, (short) 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        commands.add(item("imgStream 1bpp (Displays image from imgSave without saving)", glasses -> {
            try {
                Bitmap img1 = BitmapFactory.decodeStream(getAssets().open("tigre_304x256.png"));
                glasses.cfgWrite("DemoApp", 1, 42);
                glasses.imgStream(img1, ImgStreamFormat.MONO_1BPP, (short) 0, (short) 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        if (connectedGlasses.isColorCapable()) {
            commands.add(item("imgPicker (Displays Image from gallery)", glasses -> {
                mGetContent.launch(("image/*"));
            }));
        }

        return commands.toArray(new Map.Entry[0]);
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    ContentResolver cr = getContentResolver();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                        connectedGlasses.cfgWrite("DemoApp", 1, 42);
                        ImageData imgData = ColorImageConverter.convert(bitmap);
                        connectedGlasses.imgSave((byte) imagesSlot[6], imgData, ImgSaveFormat.RG_COLOR_8BPP);
                        connectedGlasses.clear();
                        connectedGlasses.cfgSet("DemoApp");
                        connectedGlasses.imgDisplay((byte) imagesSlot[6], (short) 0, (short) 0);
                        bitmap.recycle();
                    } catch (Exception e) {
                        Log.e("imagePicker","Error"+ e.getMessage());
                    }
                }
            }
    );

}
