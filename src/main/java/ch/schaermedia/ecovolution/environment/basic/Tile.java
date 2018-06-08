/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.basic;

import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import java.util.ArrayList;
import java.util.List;

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

    public Tile(float width, float height, int x, int y)
    {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.layers = new CompoundMix[NUMBER_OF_ATMOSPHERELAYERS];
        init();
    }

    private void init()
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = new CompoundMix(x, y, i);
        }
    }

    public void calculate(List<Tile> tiles, int range)
    {
        for (int i = 0; i < layers.length; i++)
        {
            List<CompoundMix> neighbours = new ArrayList<>();
            for (Tile tile : tiles)
            {
                neighbours.add(tile.getMixAtLayer(i));
            }
            CompoundMix higher = i < layers.length - 1 ? layers[i + 1] : null;
            CompoundMix lower = i > 0 ? layers[i - 1] : null;
            layers[i].spread(neighbours, higher, lower, range);
        }
    }

    public void update()
    {
        for (CompoundMix layer : layers)
        {
            layer.update();
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
