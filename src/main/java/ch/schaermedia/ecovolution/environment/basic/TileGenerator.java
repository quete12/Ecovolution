/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.basic;

/**
 *
 * @author Quentin
 */
public interface TileGenerator {

    public Tile generate(int x, int y, float size);
}
