/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.threads;

import ch.schaermedia.ecovolution.world.World;

/**
 *
 * @author Quentin
 */
public class AtmosUpdater extends Updater{

    private final World world;

    public AtmosUpdater(World world)
    {
        this.world = world;
    }

    @Override
    protected void update()
    {
        world.updateAtmospherics();
    }

}
