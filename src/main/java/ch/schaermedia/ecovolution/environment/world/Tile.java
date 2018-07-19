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

    private long amount_mol;
    private long energy_kj;

    private long temperature_k;
    private long pressure_kPa;
    private long volume_L;

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
        clearStats();
        long temperatureSum = 0;
        for (LayerMixture layer : layers)
        {
            layer.update();
            amount_mol += layer.getAmount_mol();
            energy_kj += layer.getEnergy_kj();
            pressure_kPa += layer.getPressure_kPa();
            volume_L += layer.getVolume_L();
            temperatureSum += layer.getTemperature_k();
        }
        temperature_k = temperatureSum / layers.length;
    }


    public void spreadHorizontal()
    {
        for (LayerMixture layer : layers)
        {
            layer.spreadHorizontal();
        }
    }

    public void importBuffers(){
        for (LayerMixture layer : layers)
        {
            layer.importBuffers();
        }
    }

    public void spreadToHigher()
    {
        for (int i = 0; i < layers.length - 1; i++)
        {
            LayerMixture layer = layers[i];
            long molesOverVolume = layer.molesOverVolume();
            if (molesOverVolume == 0)
            {
                continue;
            }
            if(molesOverVolume<0){
                throw new RuntimeException("negative moles over volume");
            }
            double percentage = molesOverVolume / layer.getAmount_mol();
            if (percentage > 1)
            {
                percentage = 1;
            }
            layer.spreadToHigher(percentage);
        }
    }

    public void spreadToLower()
    {
        for (int i = 1; i < layers.length; i++)
        {
            LayerMixture layer = layers[i];
            if(layer.getAmount_mol() == 0){
                continue;
            }
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
