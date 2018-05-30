/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Quentin
 */
public class World {

    private static final int minsize = 5;

    private Map<Integer, Map<Integer, Chunk>> chunks = new HashMap<>();

    public void init() {
        for (int x = -minsize; x <= minsize; x++) {
            Map<Integer,Chunk> col = new HashMap<>();
            for (int y = -minsize; y <= minsize; y++) {
                Chunk c = new Chunk(x, y);
                c.init();
                col.put(y, c);
            }
            chunks.put(x, col);
        }
    }

}
