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

    public static final long SIZE = 100;
    public static final long LAYER_VOLUME_L = Tile.SIZE * Tile.SIZE * Tile.SIZE;
    private final int xIdx;
    private final int yIdx;

    private long amount_mol;
    private long energy_kj;

    private long temperature_k;
    private long pressure_kPa;
    private long volume_L;

    private final List<LayerMixture> layers;
    private final int numLayers;

    public Tile(int xIdx, int yIdx, int numLayers)
    {
        this.xIdx = xIdx;
        this.yIdx = yIdx;
        this.layers = new ArrayList();
        this.numLayers = numLayers;
        initLayers();
        initTopBottom();
    }

    public void update()
    {
        clearStats();
        long temperatureSum = 0;
        temperatureSum = layers.stream().map((layer) ->
        {
            layer.update();
            return layer;
        }).map((layer) ->
        {
            amount_mol += layer.getAmount_mol();
            return layer;
        }).map((layer) ->
        {
            energy_kj += layer.getEnergy_kj();
            return layer;
        }).map((layer) ->
        {
            pressure_kPa += layer.getPressure_kPa();
            return layer;
        }).map((layer) ->
        {
            volume_L += layer.getVolume_L();
            return layer;
        }).map((layer) -> layer.getTemperature_k()).reduce(temperatureSum, (accumulator, _item) -> accumulator + _item);
        temperature_k = temperatureSum / numLayers;
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
        layers.stream().filter((layer) -> layer.hasHigher()).filter((layer) -> !(layer.getAmount_mol() == 0)).forEachOrdered((layer) ->
        {
            long molesOverVolume = layer.molesOverVolume();
            if (!(molesOverVolume == 0))
            {
                if (molesOverVolume < 0)
                {
                    throw new RuntimeException("negative moles over volume");
                }
                LayerMixture higherLayer = layer.getHigher();
                if (!(layer.getPressure_kPa() < higherLayer.getPressure_kPa()))
                {
                    double percentage = molesOverVolume / layer.getAmount_mol();
                    if (percentage > 0.95)
                    {
                        percentage = 0.95;
                    }
                    layer.spreadToHigher(percentage);
                    //didSpreadVertically = true;
                }
            }
        });
    }

    public void spreadToLower()
    {
        layers.stream().filter((layer) -> layer.hasLower()).filter((layer) -> !(layer.getAmount_mol() == 0)).forEachOrdered((layer) ->
        {
            LayerMixture lower = layer.getLower();
            long molesOverVolume = lower.molesUnderVolume();
            if (!(molesOverVolume == 0))
            {
                if (molesOverVolume < 0)
                {
                    throw new RuntimeException("negative moles under volume");
                }
                double percentage = molesOverVolume / layer.getAmount_mol();
                if (percentage > 1.0)
                {
                    percentage = 1.0;
                }
                layer.spreadToLower(percentage);
                //didSpreadVertically = true;
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

    private void clearStats()
    {
        amount_mol = 0;
        energy_kj = 0;
        temperature_k = 0;
        pressure_kPa = 0;
        volume_L = 0;
    }

    public int getxIdx()
    {
        return xIdx;
    }

    public int getyIdx()
    {
        return yIdx;
    }

    public long getAmount_mol()
    {
        return amount_mol;
    }

    public long getTemperature_k()
    {
        return temperature_k;
    }

    public long getPressure_kPa()
    {
        return pressure_kPa;
    }

    public long getVolume_L()
    {
        return volume_L;
    }
}
