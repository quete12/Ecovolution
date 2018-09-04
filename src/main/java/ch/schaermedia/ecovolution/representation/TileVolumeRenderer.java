/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.ecovolution.environment.world.Tile;
import ch.schaermedia.ecovolution.general.math.BigDouble;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TileVolumeRenderer implements TileRenderer {

    private final int layer;

    public TileVolumeRenderer(int layer)
    {
        this.layer = layer;
    }

    @Override
    public void render(PGraphics g, Tile tile)
    {
        g.noStroke();
        prepareForLayerRendering(g, tile);
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

    private void prepareForLayerRendering(PGraphics g, Tile tile)
    {
        LayerMixture layerMix = tile.getLayer(layer);
        BigDouble volume_L = layerMix.getVolume_L();
        int fill;
        if (volume_L.isZero())
        {
            fill = 0;
        } else if (volume_L.compareTo(Tile.LAYER_VOLUME_L) > 0)
        {
            float percentage = (float) ((volume_L.toDouble() / Tile.LAYER_VOLUME_L.toDouble()) - 1.0);
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) (volume_L.toDouble() / Tile.LAYER_VOLUME_L.toDouble());
            fill = g.lerpColor(g.color(0, 0, 255), g.color(255), percentage);
        }
        g.fill(fill);
    }

}
