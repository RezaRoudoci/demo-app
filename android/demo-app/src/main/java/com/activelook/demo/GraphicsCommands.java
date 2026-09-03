package com.activelook.demo;

import android.graphics.Point;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.Rotation;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class GraphicsCommands extends CommandsBase {

    @Override
    protected String getCommandGroup() {
        return "Graphics commands";
    }

    @Override
    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        List<Map.Entry<String, Consumer<Glasses>>> commands = new ArrayList<>();

        commands.add(item("clear()", glasses -> glasses.clear()));

        if (connectedGlasses.isColorCapable()) {
            commands.add(item("color(5)", glasses -> setColor(glasses,(byte) 0x05)));
            commands.add(item("color(black)",
                    glasses ->{
                        setColor(glasses,(byte) 0x00);
                    }
            )); // rien
            commands.add(item("color(red_dim)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x04);
            })); // rouge-orangé, faible intensité
            commands.add(item("color(red)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x08);
            })); // rouge-orangé, max intensité
            commands.add(item("color(green_dim)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x40);
            })); // vert-jaune, faible intensité
            commands.add(item("color(green)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x80);
            })); // vert-jaune, max intensité
            commands.add(item("color(orange)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x48);
            })); // R domine (R8/G4) -> jaune tirant orange
            commands.add(item("color(chartreuse)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x84);
            })); // G domine (R4/G8) -> jaune tirant vert
            commands.add(item("color(yellow)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x88);
            })); // R8/G8, équilibré -> jaune pur
            commands.add(item("color(amber)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x28);
            })); // R8/G2 -> orange plus proche du rouge
            commands.add(item("color(lime)",
                    glasses ->
                    {
                        setColor(glasses,(byte) 0x82);
            }));// R2/G8 -> vert plus proche du jaune
        }

        commands.add(item("points", glasses -> {
            glasses.point(new Point(200, 200));
            glasses.point(new Point(200, 210));
            glasses.point(new Point(210, 210));
            glasses.point(new Point(210, 200));
            glasses.point(new Point(205, 205));
        }));
        commands.add(item("line(0, 0, 304, 256)", glasses -> glasses.line(new Point(0, 0), new Point(304, 256))));
        commands.add(item("rect(10, 10, 290, 240)", glasses -> glasses.rect(new Point(10, 10), new Point(290, 240))));
        commands.add(item("rectf(13, 23, 20, 10)", glasses -> glasses.rectf(new Point(13, 23), new Point(20, 10))));
        commands.add(item("circ(25, 25, 11)", glasses -> glasses.circ(new Point(25, 25), (byte) 11)));
        commands.add(item("circf(25, 25, 7)", glasses -> glasses.circf(new Point(25, 25), (byte) 7)));
        commands.add(item(("txt(30, 30, 0, 1, couleur, Bonjour) " + (connectedGlasses.isColorCapable() ? "(Couleur)" : "(Niveaux de gris)") ), glasses -> glasses.txt(new Point(30, 30), Rotation.BOTTOM_LR,
                (byte) 1,
                lastColor, "Bonjour")));
        commands.add(item("polyline(3 pts)", glasses -> {
            ArrayList<Point> pts = new ArrayList<>();
            pts.add(new Point(50, 50));
            pts.add(new Point(50, 200));
            pts.add(new Point(250, 200));
            glasses.polyline(pts);
        }));
        commands.add(item("polyline(4 pts)", glasses -> {
            ArrayList<Point> pts = new ArrayList<>();
            pts.add(new Point(50, 50));
            pts.add(new Point(50, 100));
            pts.add(new Point(100, 100));
            pts.add(new Point(100, 150));
            glasses.polyline(pts);
        }));
        commands.add(item("polyline thickness(4 pts)", glasses -> {
            ArrayList<Point> pts = new ArrayList<>();
            pts.add(new Point(50, 50));
            pts.add(new Point(50, 100));
            pts.add(new Point(100, 100));
            pts.add(new Point(100, 150));
            glasses.polyline((byte)5, pts);
        }));

        return commands.toArray(new Map.Entry[0]);
    }

}
