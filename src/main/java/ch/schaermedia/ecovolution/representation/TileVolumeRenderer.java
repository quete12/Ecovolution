/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.ecovolution.environment.world.Tile;
import ch.schaermedia.ecovolution.environment.world.World;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TileVolumeRenderer implements TileRenderer {

    private final int layer;

    public TileVolumeRenderer()
    {
        layer = -1;
    }

    public TileVolumeRenderer(int layer)
    {
        this.layer = layer;
    }

    @Override
    public void render(PGraphics g, Tile tile)
    {
        g.noStroke();
        if (layer == -1)
        {
            prepareForTileRendering(g, tile);
        } else
        {
            prepareForLayerRendering(g, tile);
        }
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

    private void prepareForTileRendering(PGraphics g, Tile tile)
    {
        long volume_L = tile.getVolume_L();
        long tileVolume = Tile.LAYER_VOLUME_L * World.NUMBER_OF_LAYERS;
        int fill;
        if (volume_L > tileVolume)
        {
            float percentage = (float) (1 - ((double) volume_L / (double) tileVolume));
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) ((double) volume_L / (double) tileVolume);
            fill = g.lerpColor(g.color(0), g.color(255), percentage);
        }
        g.fill(fill);
    }

    private void prepareForLayerRendering(PGraphics g, Tile tile)
    {
        LayerMixture layerMix = tile.getLayer(layer);
        long volume_L = layerMix.getVolume_L();
        int fill;
        if (volume_L > Tile.LAYER_VOLUME_L)
        {
            float percentage = (float) (1 - ((double) volume_L / (double) Tile.LAYER_VOLUME_L));
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) ((double) volume_L / (double) Tile.LAYER_VOLUME_L);
            fill = g.lerpColor(g.color(0), g.color(255), percentage);
        }
        g.fill(fill);
    }

}
