/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.debug;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class Layer {

    public static final double MAX_VALUE = 1000;

    private double value = 0;
    private double buffer = 0;

    private final List<Layer> neighbours;
    private final int spreadSize;
    private final int layerIdx;
    private Layer top;
    private Layer bottom;

    public Layer(int layerIdx,int spreadSize)
    {
        this.layerIdx = layerIdx;
        this.neighbours = new ArrayList<>();
        this.spreadSize = spreadSize;
    }

    public void addAsneighbour(Layer neighbour)
    {
        neighbours.add(neighbour);
    }

    public void importBuffer()
    {
        value += buffer;
        buffer = 0;
    }

    public void add(double amount)
    {
        buffer += amount;
    }

    public void spreadHigher()
    {
        if(top == null){
            return;
        }
        double diff = value - MAX_VALUE;
        if (diff <= 0)
        {
            return;
        }
        if (value < diff)
        {
            diff = value;
        }
        top.add(diff);
        buffer -= diff;
    }

    public void spreadLower()
    {
        if(bottom == null){
            return;
        }
        double diff = MAX_VALUE - bottom.getValue();
        if (diff <= 0)
        {
            return;
        }
        if (value < diff)
        {
            diff = value;
        }
        bottom.add(diff);
        buffer -= diff;
    }

    public void spreadHorizontal()
    {
        double percentage = 1.0 / spreadSize;
        double perNeighbour = percentage * value;
        for (Layer neighbour : neighbours)
        {
            neighbour.add(perNeighbour);
            value -= perNeighbour;
        }
    }

    public double getValue()
    {
        return value;
    }

    public void setTop(Layer top)
    {
        this.top = top;
    }

    public void setBottom(Layer bottom)
    {
        this.bottom = bottom;
    }

    public int getLayerIdx()
    {
        return layerIdx;
    }
}
