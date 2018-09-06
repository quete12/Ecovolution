/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.representation;

import ch.schaermedia.old.ecovolution.environment.chem.compound.LayerMixture;
import ch.schaermedia.old.ecovolution.environment.chem.compound.Phase;
import ch.schaermedia.old.ecovolution.environment.world.Tile;
import ch.schaermedia.old.ecovolution.general.math.BigDouble;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TilePhaseRenderer implements TileRenderer {

    private final int layer;

    public TilePhaseRenderer(int layer)
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
        BigDouble[] phasePercentages = layerMix.getPhasePercentages();
        if(phasePercentages == null){
            g.fill(0);
            return;
        }
        float red = (float) phasePercentages[Phase.GAS.idx].mul(255, 0).toDouble();
        float green = (float) phasePercentages[Phase.SOLID.idx].mul(255, 0).toDouble();
        float blue = (float) phasePercentages[Phase.LIQUID.idx].mul(255, 0).toDouble();

        g.fill(red, green, blue);
    }
}
