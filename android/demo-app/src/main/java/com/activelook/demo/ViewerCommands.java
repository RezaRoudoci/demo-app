package com.activelook.demo;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.ConfigurationDescription;
import com.activelook.activelooksdk.types.DemoPattern;
import com.activelook.activelooksdk.types.ImageData;
import com.activelook.activelooksdk.types.ImgSaveFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewerCommands extends CommandsBase {
    private ImgSaveFormat pendingSaveFormat = ImgSaveFormat.RG_COLOR_8BPP;
    private boolean colorImageSaved = false;
    LinearLayout newLinearLayout;
    Boolean gesture = false;
    @Override
    protected String getCommandGroup() {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        newLinearLayout = new LinearLayout(ViewerCommands.this);
        newLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        newLinearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(newLinearLayout);
        return "Viewer commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        addButtons();
        List<Map.Entry<String, Consumer<Glasses>>> commands = new ArrayList<>();

        if (connectedGlasses.isColorCapable()) {
            commands.add(item("Add image (couleur)", glasses -> {
                if (colorImageSaved) {
                    snack("Une seule image couleur autorisée (contrainte mémoire des lunettes)");
                    return;
                }
                pendingSaveFormat = ImgSaveFormat.RG_COLOR_8BPP;
                mGetContent.launch("image/*");
            }));
        }
        commands.add(item("Add image (monochrome)", glasses -> {
            pendingSaveFormat = ImgSaveFormat.MONO_4BPP_HEATSHRINK_SAVE_COMP;
            mGetContent.launch("image/*");
        }));
        /*
        commands.add(item("Display first image", glasses  -> {
            glasses.clear();
            glasses.cfgSet("viewer");
            glasses.imgDisplay((byte) 0, (short) 0, (short) 0);
        }));
        */
        commands.add(item("Enable/disable gesture", glasses -> {
            gesture = !gesture;
            glasses.gesture(gesture);
            glasses.subscribeToSensorInterfaceNotifications(() ->
                    gestureNextImg()
            );
        }));
        commands.add(item("Read 'viewer' config", glasses -> {
            glasses.cfgRead(
                    "viewer",
                    r -> snack(String.format("cfgRead: %s", r))
            );
        }));
        commands.add(item("erase 'viewer' config", glasses -> {
            glasses.cfgDelete("viewer");
            colorImageSaved = false;
            ViewerCommands.this.snack(String.format("Config erased"));
            newLinearLayout.removeAllViews();
        }));

        return commands.toArray(new Map.Entry[0]);
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    ContentResolver cr = getContentResolver();
                    try {
                        toast(String.format("Saving image..."));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);
                        connectedGlasses.cfgWrite("viewer", 1, 1337);
                        connectedGlasses.cfgRead("viewer", cfgi -> {
                            if (pendingSaveFormat == ImgSaveFormat.RG_COLOR_8BPP && colorImageSaved) {
                                bitmap.recycle();
                                snack("Une seule image couleur autorisée (contrainte mémoire des lunettes)");
                                return;
                            }
                            if (pendingSaveFormat == ImgSaveFormat.RG_COLOR_8BPP) {
                                ImageData imgData = ColorImageConverter.convert(bitmap);
                                connectedGlasses.imgSave((byte) cfgi.getNbImg(), imgData, ImgSaveFormat.RG_COLOR_8BPP);
                                colorImageSaved = true;
                            } else {
                                connectedGlasses.imgSave((byte) cfgi.getNbImg(), bitmap, pendingSaveFormat);
                            }
                            bitmap.recycle();
                            connectedGlasses.cfgRead("viewer", r -> snack(String.format("Image saved : n°%s", cfgi.getNbImg())));
                            addButtons();
                        });

                    } catch (Exception e) {
                        Log.e("imagePicker","Error"+ e.getMessage());
                    }
                }
            }
    );

    private void gestureNextImg(){
        connectedGlasses.clear();
        connectedGlasses.cfgSet("viewer");
        connectedGlasses.demo(DemoPattern.IMAGE);
    }

    private Toast toast(Object data) {
        Log.d("viewerCommands", data.toString());
        Toast toast = Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    private void addButtons(){
        connectedGlasses.cfgList(configs -> {
            boolean viewerConfigExists = false;
            for (ConfigurationDescription config : configs) {
                if ("viewer".equals(config.getName())) {
                    viewerConfigExists = true;
                    break;
                }
            }
            if (!viewerConfigExists) {
                // "viewer" n'existe pas encore sur les lunettes (premier lancement,
                // ou après "erase 'viewer' config") : cfgRead("viewer") planterait
                // côté SDK (l'erreur 0xE2 renvoyée par les lunettes est mal gérée par
                // AbstractGlasses.delegateToCallback), donc on s'arrête ici.
                runOnUiThread(() -> newLinearLayout.removeAllViews());
                return;
            }
            addButtonsFromConfig();
        });
    }

    private void addButtonsFromConfig(){
        connectedGlasses.cfgRead("viewer", cfgi -> {
            runOnUiThread(() -> {
                newLinearLayout.removeAllViews();

                for(int i = 0; i< cfgi.getNbImg(); i++){
                    final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View convertView = inflater.inflate(R.layout.command_button, null);
                    convertView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    Button button = convertView.findViewById(R.id.command_button);

                    int imageId = i;
                    //Button button = new Button(ViewerCommands.this);
                    button.setText("img "+imageId);
                    newLinearLayout.addView(convertView);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            connectedGlasses.clear();
                            connectedGlasses.cfgSet("viewer");
                            connectedGlasses.imgDisplay((byte) imageId, (short) 0, (short) 0);
                        }
                    });
                }
            });
        });
    }
}
