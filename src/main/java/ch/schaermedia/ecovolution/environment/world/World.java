/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.world;

/**
 *
 * @author Quentin
 */
public class World {

    private final int numberOfLayers = 2;
    private final int neighbourRange = 3;
    private final int neighbourSquare = (2 * neighbourRange + 1) * (2 * neighbourRange + 1);
    private Tile[][] grid;
    private final int width;
    private final int height;

    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        initGrid();
    }

    public void update(){
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].update();
            }
        }
    }

    private void initGrid()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //TODO: replace with TileGenerator
                grid[x][y] = new Tile(x, y, numberOfLayers);
            }
        }
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                initNeighboursFor(x, y);
            }
        }
    }

    private void initNeighboursFor(int x, int y)
    {
        for (int xoffs = -neighbourRange; xoffs <= neighbourRange; xoffs++)
        {
            int mx = x + xoffs;
            if (mx < 0 || mx >= width || xoffs == 0)
            {
                continue;
            }
            for (int yoffs = -neighbourRange; yoffs <= neighbourRange; yoffs++)
            {
                int my = y + yoffs;
                if (my < 0 || my >= height || yoffs == 0)
                {
                    continue;
                }
                grid[x][y].addAsNeighbour(grid[mx][my]);
            }
        }
    }
}
