/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import org.joml.Vector2f;
import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 *
 * @author Quentin
 */
public class Sim extends PApplet {

    public static final int border = 5;
    private final double zoomStep = 0.01;
    private double zoom = 0.1;
    private final Vector2f worldTranslation = new Vector2f();

    @Override
    public void settings() {
        size(1200, 800, P2D);
    }

    @Override
    public void draw() {
        background(0);
        pushMatrix();
        translate(border, border);
        pushMatrix();
        Vector2f mousePos = new Vector2f(mouseX, mouseY);
        if (mousePressed) {
            Vector2f diff = mouseStart.sub(mousePos, new Vector2f());
            mouseStart = mousePos;
            worldTranslation.add(diff.negate());
        }
        translate(worldTranslation.x, worldTranslation.y);
        scale((float) zoom);

        popMatrix();
        popMatrix();
        fill(0);
        noStroke();
        rect(0, 0, width, border);
        rect(0, 0, border, height);
        rect(0, height - border, width, border);
        rect(width - border, 0, border, height);
    }

    @Override
    public void setup() {
        windowSetup();
    }

    private void windowSetup() {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(60);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        zoom -= event.getCount() * zoomStep;
    }
    private Vector2f mouseStart = new Vector2f();

    @Override
    public void mouseReleased() {
    }

    @Override
    public void mousePressed() {
        if (mouseButton == LEFT) {
            mouseStart.x = mouseX;
            mouseStart.y = mouseY;
        }
    }

    public static void main(String[] args) {
        PApplet.main(Sim.class, args);
    }

}
