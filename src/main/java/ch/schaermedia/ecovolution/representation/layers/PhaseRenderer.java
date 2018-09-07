/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation.layers;

import ch.schaermedia.ecovolution.chemics.atmospherics.LayerMixture;
import ch.schaermedia.ecovolution.chemics.atmospherics.Phase;
import ch.schaermedia.ecovolution.math.BigDouble;
import ch.schaermedia.ecovolution.representation.WorldOverlayRenderer;
import ch.schaermedia.ecovolution.world.Tile;
import ch.schaermedia.ecovolution.world.World;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class PhaseRenderer extends WorldOverlayRenderer {

    private int layerIdx;

    public PhaseRenderer(PGraphics g, World world, int layerIdx)
    {
        super(g, world);
        this.layerIdx = layerIdx;
    }

    @Override
    protected void render(PGraphics g, Tile tile)
    {
        LayerMixture layerMix = tile.getLayer(layerIdx);
        BigDouble[] phasePercentages = layerMix.getPhasePercentages();
        if (phasePercentages != null)
        {
            float red = (float) phasePercentages[Phase.GAS.idx].mul(255, 0).toDouble();
            float green = (float) phasePercentages[Phase.SOLID.idx].mul(255, 0).toDouble();
            float blue = (float) phasePercentages[Phase.LIQUID.idx].mul(255, 0).toDouble();
            g.fill(red, green, blue);
        } else
        {
            g.fill(0);
        }
        g.noStroke();
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

}
