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

    public Tile(float width, float height, int x, int y,int horizontalSpreadSize)
    {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.layers = new CompoundMix[NUMBER_OF_ATMOSPHERELAYERS];
        init(horizontalSpreadSize);
    }

    private void init(int horizontalSpreadSize)
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = new CompoundMix(x, y, i,horizontalSpreadSize);
        }
        for (int i = 0; i < layers.length; i++)
        {
            CompoundMix layer = layers[i];
            if (i > 0)
            {
                layer.setLower(getMixAtLayer(i - 1));
            }
            if (i < layers.length - 1)
            {
                layer.setHigher(getMixAtLayer(i + 1));
            }
        }
    }

    public void addAsNeighbour(Tile neighbour)
    {
        for (CompoundMix layer : layers)
        {
            int z = layer.getZ();
            layer.addAsNeibour(neighbour.getMixAtLayer(z));
        }
    }

    public void spreadHorizontal()
    {
        for (CompoundMix layer : layers)
        {
            layer.spreadHorizontal();
        }
    }

    public void spreadToLower()
    {
        for (CompoundMix layer : layers)
        {
            int layerIdx = layer.getZ();
            if (layerIdx == 0)
            {
                continue;
            }
            if (layer.getAmount_mol() == 0)
            {
                continue;
            }
            layer.spreadToLower();
        }
    }

    public void spreadToHigher()
    {

        for (CompoundMix layer : layers)
        {
            int layerIdx = layer.getZ();
            if (layerIdx == layers.length - 1)
            {
                continue;
            }
            if (layer.getAmount_mol() == 0)
            {
                continue;
            }
            layer.spreadToHigher();
        }
    }

    public void updateStats()
    {
        for (CompoundMix layer : layers)
        {
            layer.updateStats();
        }
    }

    public void updateTemperautreAndPhase()
    {
        double temperatureSum = 0;
        for (CompoundMix layer : layers)
        {
            layer.updateTemperatureAndPhaseChanges();
            temperatureSum += layer.getTemperature_K();
        }
        temperature = temperatureSum / layers.length;
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
