/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.debug;

/**
 *
 * @author Quentin
 */
public class W {

    private final int width;
    private final int height;
    private final int spreadRange;
    private final int spreadSize;

    private Tile[][] grid;

    public W(int width, int height, int spreadRange)
    {
        this.grid = new Tile[width][height];
        this.width = width;
        this.height = height;
        this.spreadRange = spreadRange;
        int side = 2 * spreadRange + 1;
        spreadSize = side * side;
        init();
    }

    private void init()
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y] = new Tile(spreadSize);
            }
        }
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                setNeighboursFor(x, y);
            }
        }
    }

    public void update()
    {
        spreadHihger();
        updateStats();
        spreadLower();
        updateStats();
        spreadHorizontal();
        updateStats();
    }

    private void updateStats()
    {

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].update();
            }
        }
    }

    private void spreadLower()
    {

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].spreadLower();
            }
        }
    }

    private void spreadHihger()
    {

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                grid[x][y].spreadHigher();
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

    private void setNeighboursFor(int x, int y)
    {
        for (int i = -spreadRange; i <= spreadRange; i++)
        {
            if (i == 0)
            {
                continue;
            }
            int mx = x + i;
            if (mx < 0 || mx >= width)
            {
                continue;
            }
            for (int j = -spreadRange; j <= spreadRange; j++)
            {
                if (j == 0)
                {
                    continue;
                }
                int my = y + j;
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

}
