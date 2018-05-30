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
public class Chunk {

    private static final int CHUNKSIZE = 25;

    private final int xIdx;
    private final int yIdx;
    private final int globalxId;
    private final int globalyId;

    private Tile[][] grid;

    public Chunk(int xIdx, int yIdx) {
        this.xIdx = xIdx;
        this.yIdx = yIdx;
        this.globalxId = xIdx*CHUNKSIZE;
        this.globalyId = yIdx*CHUNKSIZE;
        grid = new Tile[CHUNKSIZE][CHUNKSIZE];
    }

    public void init() {
        for (int x = 0; x < CHUNKSIZE; x++) {
            for (int y = 0; y < CHUNKSIZE; y++) {
                grid[x][y] = new Tile();
            }
        }
    }

    public boolean isActive() {
        return true;
    }

    public void update() {

    }

    public void draw() {

    }
}
