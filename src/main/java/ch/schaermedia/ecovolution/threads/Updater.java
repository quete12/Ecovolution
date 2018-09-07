/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.threads;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Quentin
 */
public abstract class Updater implements Runnable {

    private int ups = 30;
    private long targetCycleTime;
    private long cycletime;
    private long duration;

    private boolean shutdown = false;
    private boolean running;

    public Updater()
    {
        calculateCycleTime(30);
    }

    public Updater(int ups)
    {
        calculateCycleTime(ups);
    }

    protected abstract void update();

    public void shutdown()
    {
        shutdown = true;
    }

    @Override
    public void run()
    {
        running = true;
        while (!shutdown)
        {
            long starttime = System.currentTimeMillis();
            update();
            this.duration = System.currentTimeMillis() - starttime;
            if (duration < targetCycleTime)
            {
                long diff = targetCycleTime - duration;
                try
                {
                    Thread.sleep(diff);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            this.cycletime = System.currentTimeMillis() - starttime;
        }
        running = false;
    }

    /**
     *
     * @return the duration of one cycle
     */
    public long getCycletime()
    {
        return cycletime;
    }

    /**
     *
     * @return the duration of calculation
     */
    public long getDuration()
    {
        return duration;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setUps(int ups)
    {
        calculateCycleTime(ups);
    }

    private void calculateCycleTime(int ups)
    {
        targetCycleTime = 1000 / ups;
    }
}
