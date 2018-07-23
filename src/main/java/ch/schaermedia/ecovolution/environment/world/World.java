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
    public static final int NEIGHBOUR_RANGE = 4;
    private static final int NEIGHBOUR_SQARE_SIDE = 2 * NEIGHBOUR_RANGE + 1;
    public static final int NEIGHBOUR_SQUARED = NEIGHBOUR_SQARE_SIDE * NEIGHBOUR_SQARE_SIDE;
    public static final double HORIZONTAL_SPREAD_PERCENTAGE = 1.0 / (double) NEIGHBOUR_SQUARED;

    private Tile[][] grid;
    private final int width;
    private final int height;

    private final ExecutorService threadPool;

    private long amount_mol;

    public World(int width, int height)
    {
        this.width = width;
        this.height = height;
        initGrid();
        this.threadPool = Executors.newFixedThreadPool(8);
    }

    public void update()
    {
        //calculateParallel();
        calculate();
        updateTiles();
    }

    private void updateTiles(){
        amount_mol = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].update();
                amount_mol += grid[x][y].getAmount_mol();
            }
        }
    }

    public void calculate()
    {
        spreadToHigher();
        updateTiles();
        spreadToLower();
        updateTiles();
        spreadHorizontal();
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

    private void importBuffers()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].importBuffers();
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

    public void calculateParallel()
    {
        spreadToHigherParallel();
        importBuffersParallel();
        spreadToLowerParallel();
        importBuffersParallel();
        spreadHorizontalParallel();
    }

    private void importBuffersParallel()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].importBuffers();
            }
        }
    }

    private void spreadToHigherParallel()
    {
        List<Callable<Void>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(new SpreadTask(SpreadTask.SpreadType.TO_HIGHER, grid[x][y]));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void spreadHorizontalParallel()
    {
        List<Callable<Void>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(new SpreadTask(SpreadTask.SpreadType.HORIZONTAL, grid[x][y]));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void spreadToLowerParallel()
    {
        List<Callable<Void>> tasks = new ArrayList();
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                tasks.add(new SpreadTask(SpreadTask.SpreadType.TO_LOWER, grid[x][y]));
            }
        }
        try
        {
            threadPool.invokeAll(tasks);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static class SpreadTask implements Callable<Void> {

        private enum SpreadType {
            HORIZONTAL,
            TO_LOWER,
            TO_HIGHER,;
        }

        private final SpreadType type;
        private final Tile tile;

        public SpreadTask(SpreadType type, Tile tile)
        {
            this.type = type;
            this.tile = tile;
        }

        @Override
        public Void call() throws Exception
        {
            switch (type)
            {
                case HORIZONTAL:
                    tile.spreadHorizontal();
                    break;
                case TO_LOWER:
                    tile.spreadToLower();
                    break;
                case TO_HIGHER:
                    tile.spreadToHigher();
                    break;
                default:
                    throw new AssertionError(type.name());
            }
            return null;
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
