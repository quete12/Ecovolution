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
public class World {

    private final int width = 20;
    private final int height = 20;

    private final int spreadRange = 3;

    private Tile[][] grid;

    public World() {
        this.grid = new Tile[width][height];
        init();
    }

    private void init() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Tile();
            }
        }
    }

    public void update() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].calculate(getNeighbours(spreadRange, x, y));
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y].update();
            }
        }
    }

    private List<Tile> getNeighbours(int range, int x, int y) {
        List<Tile> neighbours = new ArrayList<>();
        for (int i = -range; i <= range; i++) {
            int myX = x + i;
            if (myX < 0 || myX > width) {
                continue;
            }
            for (int j = -range; j <= range; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int myY = y + j;
                if (myY < 0 || myY > height) {
                    continue;
                }
                neighbours.add(grid[myX][myY]);
            }
        }
        return neighbours;
    }
}
