/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.old.ecovolution.general;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Quentin
 */
public class Updater implements Runnable {

    private List<Updateable> items;
    private int ups;
    private boolean shutdown;

    public Updater()
    {
        this.items = new ArrayList();
        this.ups = 30;
    }

    public Updater(int ups)
    {
        this.items = new ArrayList();
        this.ups = ups;
    }

    public void registerUpdateable(Updateable item)
    {
        items.add(item);
    }

    public void removeUpdateable(Updateable item)
    {
        items.remove(item);
    }

    @Override
    public void run()
    {
        while (!shutdown)
        {
            items.forEach((item) ->
            {
                item.update();
            });
        }
    }

}
