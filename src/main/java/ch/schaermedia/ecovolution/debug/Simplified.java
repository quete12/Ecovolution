/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.debug;

import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class Simplified extends PApplet {

    //TODO: move Configuration!
    private static final int WORLD_WIDTH = 30;
    private static final int WORLD_HEIGHT = 30;
    private static final int SPREAD_RANGE = 3;
    private static final int FRAMERATE = 60;

    private W world;

    private PGraphics[] layerGraphics;

    @Override
    public void settings()
    {
        size(1900, 1000, P2D);
    }

    @Override
    public void draw()
    {
        world.update();
        int addRange = 1;
        int x = 5;
        int y = 5;
        for (int i = -addRange; i <= addRange; i++)
        {
            int mx = x + i;
            for (int j = -addRange; j <= addRange; j++)
            {
                int my = y + j;
                world.getGrid()[mx][my].getLayer(0).add(300);
                world.getGrid()[mx][my].getLayer(0).importBuffer();
            }
        }
        background(255);
        pushMatrix();
        for (int i = 0; i < layerGraphics.length; i++)
        {
            renderLayer(i);
            image(layerGraphics[i], i * (WORLD_WIDTH + 1) * Tile.SIZE, Tile.SIZE);
        }
        popMatrix();
    }

    private void renderLayer(int idx)
    {
        PGraphics g = layerGraphics[idx];
        g.beginDraw();
        for (int i = 0; i < WORLD_WIDTH; i++)
        {
            for (int j = 0; j < WORLD_HEIGHT; j++)
            {
                Layer layer = world.getGrid()[i][j].getLayer(idx);
                float value = (float) ((layer.getValue() / Layer.MAX_VALUE) * 255);
                g.fill(value);
                g.rect(i * Tile.SIZE, j * Tile.SIZE, Tile.SIZE, Tile.SIZE);
            }
        }
        g.endDraw();
    }

    @Override
    public void setup()
    {
        windowSetup();
        worldSetup();
        graphicsSetup();
    }

    private void windowSetup()
    {
        surface.setResizable(true);
        surface.setTitle("Evolution Simulation");
        surface.setFrameRate(FRAMERATE);
    }

    private void worldSetup()
    {
        world = new W(WORLD_WIDTH, WORLD_WIDTH, SPREAD_RANGE);
    }

    private void graphicsSetup()
    {
        layerGraphics = new PGraphics[3];
        for (int i = 0; i < layerGraphics.length; i++)
        {
            layerGraphics[i] = createGraphics((int) Tile.SIZE * WORLD_WIDTH, (int) Tile.SIZE * WORLD_HEIGHT);
        }
    }

    public static void main(String[] args)
    {
        PApplet.main(Simplified.class, args);
    }

}
