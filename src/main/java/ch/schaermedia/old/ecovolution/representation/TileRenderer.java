/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.representation;

import ch.schaermedia.old.ecovolution.environment.world.Tile;
import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public interface TileRenderer {

    public void render(PGraphics g, Tile tile);
}
