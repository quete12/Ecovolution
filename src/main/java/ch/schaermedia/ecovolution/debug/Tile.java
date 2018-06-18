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
public class Tile {

    public static final float SIZE = 10f;

    private final Layer[] layers;

    public Tile(int spreadSize)
    {
        layers = new Layer[3];
        init(spreadSize);
    }

    private void init(int spreadSize)
    {
        for (int i = 0; i < layers.length; i++)
        {
            layers[i] = new Layer(i, spreadSize);
        }
        for (int i = 0; i < layers.length; i++)
        {
            Layer layer = layers[i];
            if (i > 0)
            {
                layer.setBottom(layers[i - 1]);
            }
            if (i < layers.length - 1)
            {
                layer.setTop(layers[i + 1]);
            }
        }
    }

    public void addAsNeighbour(Tile neighbour)
    {
        for (Layer layer : layers)
        {
            layer.addAsneighbour(neighbour.getLayer(layer.getLayerIdx()));
        }
    }

    public void spreadLower(){
        for (Layer layer : layers)
        {
            layer.spreadLower();
        }
    }

    public void spreadHigher(){
        for (Layer layer : layers)
        {
            layer.spreadHigher();
        }
    }

    public void spreadHorizontal(){
        for (Layer layer : layers)
        {
            layer.spreadHorizontal();
        }
    }

    public void update(){
        for (Layer layer : layers)
        {
            layer.importBuffer();
        }
    }

    public Layer getLayer(int idx)
    {
        return layers[idx];
    }
}
