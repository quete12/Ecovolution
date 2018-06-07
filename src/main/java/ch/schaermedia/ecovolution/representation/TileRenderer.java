/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.representation;

import ch.schaermedia.ecovolution.environment.basic.Tile;
import ch.schaermedia.ecovolution.environment.chem.CompoundMix;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public class TileRenderer {

    public void render(Tile tile, PGraphics g) {
        g.noStroke();
        g.fill((float) (tile.getMixAtLayer(0).getVolume_L() / CompoundMix.STATIC_VOLUME_L));
        g.rect(tile.getX() * tile.getWidth(), tile.getY() * tile.getHeight(), tile.getWidth(), tile.getHeight());
    }
}
