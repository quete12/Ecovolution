/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin
 */
public class World {

    public static final float TILE_SIZE = 10f;
    private static final int NUM_PARALLEL_UPDATES = 4;

    private final ExecutorService threadpool;

    private final int width;
    private final int height;

    private final int spreadRange;

    private final TileGenerator tileGenerator;
    private final int numberOfMixes;

    private double worldTemeprature;

    private final Tile[][] grid;

    public World(int width, int height, int spreadRange, TileGenerator tileGenerator)
    {
        this.width = width;
        this.height = height;
        this.spreadRange = spreadRange;
        this.tileGenerator = tileGenerator;
        this.grid = new Tile[width][height];
        this.numberOfMixes = width * height * 3;
        init();
        this.threadpool = Executors.newFixedThreadPool(NUM_PARALLEL_UPDATES);
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
        generateTiles();
        setNeighbours();
    }

    private void generateTiles()
    {
        int side = 2 * spreadRange + 1;
        int horizontalSpreadSize = side * side;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y] = tileGenerator.generate(x, y, TILE_SIZE, horizontalSpreadSize);
            }
        }
    }

    private void setNeighbours()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                addNeighbours(spreadRange, x, y);
            }
        }
    }

    private void spreadToLower()
    {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::spreadToLower));
            }
        }
        try
        {
            threadpool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void spreadToHigher()
    {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::spreadToHigher));
            }
        }
        try
        {
            threadpool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void spreadHorizontal()
    {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::spreadHorizontal));
            }
        }
        try
        {
            threadpool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateStats()
    {
        List<Callable<Object>> tasks = new ArrayList<>();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::updateStats));
            }
        }
        try
        {
            threadpool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTemperautreAndPhase()
    {
        double temperatureSum = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].updateTemperautreAndPhase();
                temperatureSum += grid[x][y].getTemperature();
            }
        }
        worldTemeprature = temperatureSum / numberOfMixes;

    }

    public void update()
    {
        spreadToHigher();
        updateStats();
        spreadToLower();
        updateStats();
        spreadHorizontal();
        updateStats();
        updateTemperautreAndPhase();
        updateStats();
    }

    public double getWorldTemeprature()
    {
        return worldTemeprature;
    }

    private void addNeighbours(int range, int x, int y)
    {
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
                grid[x][y].addAsNeighbour(grid[myX][myY]);
            }
        }
    }

    public Tile[][] getGrid()
    {
        return grid;
    }
}
