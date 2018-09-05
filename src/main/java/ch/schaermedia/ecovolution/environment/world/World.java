/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.world;

import ch.schaermedia.ecovolution.general.math.BigDouble;

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
    public static final int NEIGHBOUR_RANGE = 3;
    private static final int NEIGHBOUR_SQARE_SIDE = 2 * NEIGHBOUR_RANGE + 1;
    public static final int NEIGHBOUR_SQUARED = NEIGHBOUR_SQARE_SIDE * NEIGHBOUR_SQARE_SIDE;
    public static final BigDouble HORIZONTAL_SPREAD_PERCENTAGE = BigDouble.ONE.div(new BigDouble(NEIGHBOUR_SQUARED, 0));

    private Tile[][] grid;
    private final int width;
    private final int height;
    private final TileGenerator generator;

    private UpdateState state;

    public World(int width, int height, TileGenerator generator)
    {
        this.width = width;
        this.height = height;
        this.state = UpdateState.HORIZONTAL;
        this.generator = generator;
        initGrid();
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

    private void initGrid()
    {
        grid = new Tile[width][height];
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y] = generator.generate(x, y, NUMBER_OF_LAYERS);
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
            if (mx < 0 || mx >= width)
            {
                continue;
            }
            for (int yoffs = -NEIGHBOUR_RANGE; yoffs <= NEIGHBOUR_RANGE; yoffs++)
            {
                if (xoffs == 0 && yoffs == 0)
                {
                    //Exclude itself from neighbours
                    continue;
                }
                int my = y + yoffs;
                if (my < 0 || my >= height)
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
