/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.environment.basic;

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
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y] = tileGenerator.generate(x, y, TILE_SIZE);
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
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //Spread to Lower
            }
        }
    }

    private void refreshStats()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //refresh stats
            }
        }
    }

    private void spreadToHigher()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //spread to higher
            }
        }
    }

    private void spreadHorizontal()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //spread horizontal
            }
        }
    }

    private void updateStats()
    {
        double temperatureSum = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                //update stats
                temperatureSum += grid[x][y].getTemperature();
            }
        }
        worldTemeprature = temperatureSum / numberOfMixes;
    }

    public void update()
    {
        spreadToLower();
        refreshStats();
        spreadToHigher();
        refreshStats();
        spreadHorizontal();
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
