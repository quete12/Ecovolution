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

    private enum UpdateState {
        VERTICAL,
        HORIZONTAL,;
    }

    public static final int NUMBER_OF_LAYERS = 3;
    public static final int NEIGHBOUR_RANGE = 4;
    private static final int NEIGHBOUR_SQARE_SIDE = 2 * NEIGHBOUR_RANGE + 1;
    public static final int NEIGHBOUR_SQUARED = NEIGHBOUR_SQARE_SIDE * NEIGHBOUR_SQARE_SIDE;
    public static final double HORIZONTAL_SPREAD_PERCENTAGE = 1.0 / (double) NEIGHBOUR_SQUARED;

    private Tile[][] grid;
    private final int width;
    private final int height;

    private final ExecutorService threadPool;

    private UpdateState state;

    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.state = UpdateState.HORIZONTAL;
        initGrid();
        this.threadPool = Executors.newFixedThreadPool(8);
    }

    public void update()
    {
        spreadAndUpdateTiles();
    }

    private void nextState()
    {
        switch (state)
        {
            case VERTICAL:
                state = UpdateState.HORIZONTAL;
                break;
            case HORIZONTAL:
                state = UpdateState.VERTICAL;
                break;
            default:
                throw new AssertionError(state.name());
        }
    }

    private void spreadAndUpdateTiles()
    {
        switch (state)
        {
            case VERTICAL:
                spreadToHigher();
                updateTiles();
                spreadToLower();
                updateTiles();
                break;
            case HORIZONTAL:
                spreadHorizontal();
                updateTiles();
                break;
            default:
                throw new AssertionError(state.name());
        }
        nextState();
    }

    private void updateTiles()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].update();
            }
        }
    }

    private void spreadToHigher()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].spreadToHigher();
            }
        }
    }

    private void spreadHorizontal()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].spreadHorizontal();
            }
        }
    }

    private void spreadToLower()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].spreadToLower();
            }
        }
    }

    private void updateParallel()
    {
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
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].importBuffers();
            }
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
}
