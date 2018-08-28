/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

/**
 *
 * @author Quentin
 */
public class Consts {

    public static final int PRESCISION = 25000;

    public static final long STANDARD_PRESSURE_kPa = toLong(101.325);
    public static final long GAS_CONSTANT_L_kPa_K = toLong(8.3144598);
    public static final long CELSIUS_TO_KELVIN_CONVERSION = toLong(273.15);

    public static double toDouble(long value)
    {
        return value / (double) PRESCISION;
    }

    public static long toLong(double value)
    {
        return (long) (value * PRESCISION);
    }

}
