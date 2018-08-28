/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.ecovolution.environment.world.Tile;
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
        long moles = layerMix.getAmount_mol();
        long molLimit = 10000000;
        int fill;
        if (moles == 0)
        {
            fill = 0;
        } else if (moles > molLimit)
        {
            float percentage = (float) (((double) moles / (double) molLimit) - 1.0);
            fill = g.lerpColor(g.color(255), g.color(255, 0, 0), percentage);
        } else
        {
            float percentage = (float) ((double) moles / (double) molLimit);
            fill = g.lerpColor(g.color(0, 0, 255), g.color(255), percentage);
        }
        g.fill(fill);
    }

}
