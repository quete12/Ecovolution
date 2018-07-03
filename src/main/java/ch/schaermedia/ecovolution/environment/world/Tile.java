/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.world;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;

/**
 *
 * @author Quentin
 */
public class Tile {

    public static final long SIZE = 100;
    public static final long LAYER_VOLUME_L = Tile.SIZE * Tile.SIZE * Tile.SIZE;
    private final int xIdx;
    private final int yIdx;

    private final LayerMixture[] layers;

    public Tile(int xIdx, int yIdx, int numLayers)
    {
        this.xIdx = xIdx;
        this.yIdx = yIdx;
        this.layers = new LayerMixture[numLayers];
        initLayers();
        initTopBottom();
    }

    public void update()
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i].update();
        }
    }

    public void calculate()
    {
        spreadToHigher();
        spreadToLower();
        spreadHorizontal();
    }

    private void spreadHorizontal()
    {
        for (LayerMixture layer : layers)
        {
            layer.spreadHorizontal();
        }
    }

    private void spreadToHigher()
    {
        for (int i = 0; i < layers.length - 1; i++)
        {
            LayerMixture layer = layers[i];
            long molesOverVolume = layer.molesOverVolume();
            if (molesOverVolume == 0)
            {
                continue;
            }
            double percentage = molesOverVolume / layer.getAmount_mol();
            if (percentage > 1)
            {
                percentage = 1;
            }
            layer.spreadToHigher(percentage);
        }
    }

    private void spreadToLower()
    {
        for (int i = 1; i < layers.length; i++)
        {
            LayerMixture layer = layers[i];
            LayerMixture lower = layers[i - 1];
            long molesOverVolume = lower.molesUnderVolume();
            if (molesOverVolume == 0)
            {
                continue;
            }
            double percentage = molesOverVolume / layer.getAmount_mol();
            if (percentage > 1)
            {
                percentage = 1;
            }
            layer.spreadToLower(percentage);
        }
    }

    public void addAsNeighbour(Tile neighbour)
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i].addAsNeighbour(neighbour.getLayer(i));
        }
    }

    public LayerMixture getLayer(int layer)
    {
        return layers[layer];
    }

    private void initLayers()
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = new LayerMixture(LAYER_VOLUME_L);
        }
    }

    private void initTopBottom()
    {
        for (int i = 0; i < layers.length; i++)
        {
            if (i > 0)
            {
                layers[i].setLower(layers[i - 1]);
            }
            if (i < layers.length - 1)
            {
                layers[i].setHigher(layers[i + 1]);
            }
        }
    }
}
