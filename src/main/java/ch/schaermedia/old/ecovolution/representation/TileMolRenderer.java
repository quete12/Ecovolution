/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.representation;

import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.math.BigDouble;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TileMolRenderer implements TileRenderer {

    private final int layer;

    public TileMolRenderer(int layer)
    {
        this.layer = layer;
    }

    @Override
    public void render(PGraphics g, Tile tile)
    {
        prepareForLayerRendering(g, tile);
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

    private void prepareForLayerRendering(PGraphics g, Tile tile)
    {
        LayerMixture layerMix = tile.getLayer(layer);
        BigDouble moles = layerMix.amount_mol();
        BigDouble molLimit = new BigDouble(10000000, 0);
        int fill;
        if (moles.isZero())
        {
            fill = 0;
        } else if (moles.compareTo(molLimit) > 0)
        {
            float percentage = (float) ((moles.toDouble() / molLimit.toDouble()) - 1.0);
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) (moles.toDouble() / molLimit.toDouble());
            fill = g.lerpColor(g.color(0, 0, 255), g.color(255), percentage);
        }
        g.fill(fill);
    }

}
