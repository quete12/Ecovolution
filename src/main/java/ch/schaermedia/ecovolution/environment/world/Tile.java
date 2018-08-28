/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.world;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class Tile {

    public static final long SIZE = 50;
    public static final long LAYER_VOLUME_L = Tile.SIZE * Tile.SIZE * Tile.SIZE;
    private final int xIdx;
    private final int yIdx;

    private final List<LayerMixture> layers;
    private final int numLayers;

    private int height;

    public Tile(int xIdx, int yIdx, int numLayers, int height)
    {
        this.xIdx = xIdx;
        this.yIdx = yIdx;
        this.layers = new ArrayList();
        this.numLayers = numLayers;
        this.height = height;
        initLayers();
        initTopBottom();
    }

    public void update()
    {
        layers.forEach((layer) ->
        {
            layer.update();
        });
    }

    public void spreadHorizontal()
    {
        layers.forEach((layer) ->
        {
            layer.spreadHorizontal();
        });
    }

    public void spreadToHigher()
    {
        layers.stream().filter((layer) -> layer.hasHigher()).filter((layer) -> layer.getAmount_mol() > 0).forEachOrdered((layer) ->
        {
            long molesOverVolume = layer.molesOverVolume();
            if (molesOverVolume > 0)
            {
                LayerMixture higherLayer = layer.getHigher();
                if (layer.getPressure_kPa() > higherLayer.getPressure_kPa())
                {
                    layer.spreadToHigher(molesOverVolume);
                }
            }
        });
    }

    public void spreadToLower()
    {
        layers.stream().filter((layer) -> layer.hasLower()).filter((layer) -> layer.getAmount_mol() > 0).forEachOrdered((layer) ->
        {
            LayerMixture lower = layer.getLower();
            long molesUnderVolume = lower.molesUnderVolume();
            if (molesUnderVolume > 0)
            {
                layer.spreadToLower(molesUnderVolume);
            }
        });
    }

    public void addAsNeighbour(Tile neighbour)
    {
        layers.forEach((layer) ->
        {
            layer.addAsNeighbour(neighbour.getLayer(layer.getLayerIdx()));
        });
    }

    public LayerMixture getLayer(int layer)
    {
        return layers.get(layer);
    }

    private void initLayers()
    {
        for (int i = 0; i < numLayers; i++)
        {
            layers.add(new LayerMixture(LAYER_VOLUME_L, i));
        }
    }

    private void initTopBottom()
    {
        for (int i = 0; i < layers.size(); i++)
        {
            if (i > 0)
            {
                layers.get(i).setLower(layers.get(i - 1));
            }
            if (i < layers.size() - 1)
            {
                layers.get(i).setHigher(layers.get(i + 1));
            }
        }
    }

    public int getxIdx()
    {
        return xIdx;
    }

    public int getyIdx()
    {
        return yIdx;
    }

    public int getHeight()
    {
        return height;
    }
}
