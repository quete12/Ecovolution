/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.basic;

import ch.schaermedia.ecovolution.environment.chem.CompoundMix;

/**
 *
 * @author Quentin
 */
public class Tile {

    private static final int NUMBER_OF_ATMOSPHERELAYERS = 3;

    private final float width;
    private final float height;

    private final int x;
    private final int y;

    private final CompoundMix[] layers;

    private double temperature;

    public Tile(float width, float height, int x, int y)
    {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.layers = new CompoundMix[NUMBER_OF_ATMOSPHERELAYERS];
        init();
    }

    public void addAsNeighbour(Tile neighbour){
        for (CompoundMix layer : layers)
        {
            layer.addAsNeibour(neighbour.getMixAtLayer(layer.getZ()));
        }
    }

    private void init()
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = new CompoundMix(x, y, i);
        }
    }

    public CompoundMix[] getLayers()
    {
        return layers;
    }

    public CompoundMix getMixAtLayer(int layer)
    {
        return layers[layer];
    }

    public double getTemperature()
    {
        return temperature;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
