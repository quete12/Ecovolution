/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.representation;

import ch.schaermedia.ecovolution.world.Tile;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TileHeightRenderer implements TileRenderer {

    @Override
    public void render(PGraphics g, Tile tile)
    {
        int fill = g.lerpColor(g.color(0), g.color(255), (float) ((double) tile.getHeight() / 100.0));
        g.fill(fill);
        g.rect(tile.getxIdx() * Tile.SIZE, tile.getyIdx() * Tile.SIZE, Tile.SIZE, Tile.SIZE);
    }

}
