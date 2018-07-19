/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.world;

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

    public static final int NUMBER_OF_LAYERS = 3;
    public static final int NEIGHBOUR_RANGE = 3;
    public static final int NEIGHBOUR_SQUARED = (2 * NEIGHBOUR_RANGE + 1) * (2 * NEIGHBOUR_RANGE + 1);
    public static final double HORIZONTAL_SPREAD_PERCENTAGE = 1.0 / (double) NEIGHBOUR_SQUARED;

    private ExecutorService threadPool;

    private Tile[][] grid;
    private final int width;
    private final int height;

    private long amount_mol;

    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        initGrid();
        threadPool = Executors.newFixedThreadPool(2);
    }

    public void update()
    {
        calculate();
        updateAll();
        amount_mol = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                amount_mol += grid[x][y].getAmount_mol();
            }
        }
    }

    private void updateAll(){
        List<Callable<Object>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::update));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void calculate()
    {
        spreadToHigher();
        importBuffers();
        spreadToLower();
        importBuffers();
        spreadHorizontal();
    }

    private void spreadToHigher()
    {
        List<Callable<Object>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::spreadToHigher));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void importBuffers()
    {
        List<Callable<Object>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::importBuffers));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void spreadToLower()
    {
        List<Callable<Object>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::spreadToLower));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void spreadHorizontal()
    {
        List<Callable<Object>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(Executors.callable(grid[x][y]::spreadHorizontal));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initGrid()
    {
        grid = new Tile[width][height];
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //TODO: replace with TileGenerator
                grid[x][y] = new Tile(x, y, NUMBER_OF_LAYERS);
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
        for (int xoffs = -NEIGHBOUR_RANGE; xoffs <= NEIGHBOUR_RANGE; xoffs++)
        {
            int mx = x + xoffs;
            if (mx < 0 || mx >= width || xoffs == 0)
            {
                continue;
            }
            for (int yoffs = -NEIGHBOUR_RANGE; yoffs <= NEIGHBOUR_RANGE; yoffs++)
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

    public Tile[][] getGrid()
    {
        return grid;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public long getAmount_mol()
    {
        return amount_mol;
    }
}
