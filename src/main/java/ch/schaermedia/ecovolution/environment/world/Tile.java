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
    public static final long LAYER_VOLUME_L = SIZE * SIZE * SIZE;
    private final int xIdx;
    private final int yIdx;

    private final LayerMixture[] layers;

    public Tile(int xIdx, int yIdx, int numLayers)
    {
        this.xIdx = xIdx;
        this.yIdx = yIdx;
        this.layers = new LayerMixture[numLayers];
        initLayers();
    }

    public void update(){
        for (int i = 0; i < layers.length; i++)
        {
            layers[i].update(LAYER_VOLUME_L);
        }
    }

    public void addAsNeighbour(Tile neighbour)
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i].addAsNeighbour(neighbour.getLayer(i));
        }
    }

    public LayerMixture getLayer(int layer){
        return layers[layer];
    }

    private void initLayers()
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = new LayerMixture();
        }
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
