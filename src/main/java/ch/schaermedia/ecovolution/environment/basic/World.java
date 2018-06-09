/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.basic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class World {

    public static final float TILE_SIZE = 10f;

    private final int width;
    private final int height;

    private final int spreadRange;

    private final TileGenerator tileGenerator;

    private double worldTemeprature;

    private Tile[][] grid;

    public World(int width, int height, int spreadRange, TileGenerator tileGenerator)
    {
        this.width = width;
        this.height = height;
        this.spreadRange = spreadRange;
        this.tileGenerator = tileGenerator;
        this.grid = new Tile[width][height];
        init();
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    private void init()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y] = tileGenerator.generate(x, y, TILE_SIZE);
            }
        }
    }

    public void update()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].calculate(getNeighbours(spreadRange, x, y), spreadRange);
            }
        }
        double temperatureSum = 0;
        int counter = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].update();
                temperatureSum += grid[x][y].getTemperature();
                counter ++;
            }
        }
        worldTemeprature = temperatureSum / counter;
    }

    public double getWorldTemeprature()
    {
        return worldTemeprature;
    }

    private List<Tile> getNeighbours(int range, int x, int y)
    {
        List<Tile> neighbours = new ArrayList<>();
        for (int i = -range; i <= range; i++)
        {
            int myX = x + i;
            if (myX < 0 || myX >= width)
            {
                continue;
            }
            for (int j = -range; j <= range; j++)
            {
                if (i == 0 && j == 0)
                {
                    continue;
                }
                int myY = y + j;
                if (myY < 0 || myY >= height)
                {
                    continue;
                }
                neighbours.add(grid[myX][myY]);
            }
        }
        return neighbours;
    }

    public Tile[][] getGrid()
    {
        return grid;
    }
}
