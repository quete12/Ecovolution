/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general;

import ch.schaermedia.ecovolution.environment.world.World;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin
 */
public class AtmosphericUpdater implements Runnable {

    private final World world;
    private int ups;

    private int msPerCycle;
    private boolean keepAlive = true;
    private boolean running = false;
    private long cycleTime = 0;

    public AtmosphericUpdater(World world)
    {
        this.world = world;
        this.ups = 30;

        this.msPerCycle = 1000 / this.ups;
    }

    @Override
    public void run()
    {
        this.running = true;
        while (keepAlive)
        {
            long start = System.currentTimeMillis();
            world.update();
            cycleTime = System.currentTimeMillis() - start;
            if (cycleTime < msPerCycle)
            {
                long diff = msPerCycle - cycleTime;
                try
                {
                    Thread.sleep(diff);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(AtmosphericUpdater.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        this.running = false;
    }

    public void shutdown()
    {
        keepAlive = false;
    }

    public boolean isRunning()
    {
        return running;
    }

    public long getCycleTime()
    {
        return cycleTime;
    }

}
