/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

/**
 *
 * @author Quentin
 */
public class Tile {
    private static final int NUMBER_OF_ATMOSPHERELAYERS = 3;

    private CompoundMix[] layers;

    public Tile() {
        this.layers = new CompoundMix[NUMBER_OF_ATMOSPHERELAYERS];
    }
}
