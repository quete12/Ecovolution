/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.world;

import processing.core.PApplet;

/**
 *
 * @author Quentin
 */
public class DefaultWorldGen implements TileGenerator {

    private final PApplet applet;

    public DefaultWorldGen(PApplet applet)
    {
        this.applet = applet;
    }

    @Override
    public Tile generate(int x, int y, int numLayers)
    {
        int height = (int) (applet.noise((float) (x * 0.1), (float) ((y+1000) * 0.1)) * 100);
        System.out.println("Tile[" + x + "][" + y + "] hegiht: " + height + " created");
        Tile result = new Tile(x, y, numLayers, height);
        return result;
    }

}
