/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.ArrayList;
import java.util.List;

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

    public void calculate(List<Tile> tiles) {
        for (int i = 0; i < layers.length; i++) {
            List<CompoundMix> neighbours = new ArrayList<>();
            for (Tile tile : tiles) {
                neighbours.add(tile.getMixAtLayer(i));
            }
            CompoundMix higher = i < layers.length - 1 ? layers[i + 1] : null;
            CompoundMix lower = i > 0 ? layers[i - 1] : null;
            layers[i].spread(neighbours, higher, lower);
        }
    }

    public void update() {
        for (CompoundMix layer : layers) {
            layer.update();
        }
    }

    public CompoundMix getMixAtLayer(int layer) {
        return layers[layer];
    }
}
