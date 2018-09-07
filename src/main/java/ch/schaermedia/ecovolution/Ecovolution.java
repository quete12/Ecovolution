/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution;

import ch.schaermedia.ecovolution.threads.RenderThread;
import processing.core.PApplet;

/**
 *
 * @author Quentin
 */
public class Ecovolution {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        PApplet.main(RenderThread.class, args);
    }

}
