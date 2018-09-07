/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.chemics.atmospherics;

/**
 *
 * @author Quentin
 */
public enum Phase {
    SOLID(0), LIQUID(1), GAS(2), SUPERCRITICAL_FLUID(3);

    public final int idx;

    private Phase(int idx)
    {
        this.idx = idx;
    }

    public static Phase fromIdx(int idx)
    {
        for (Phase value : values())
        {
            if (value.idx == idx)
            {
                return value;
            }
        }
        return null;
    }

}
