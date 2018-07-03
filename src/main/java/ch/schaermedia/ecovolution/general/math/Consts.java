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

    public static final int PRESCISION = 1000000;

    public static final long STANDARD_PRESSURE_kPa = (long) (101.325 * PRESCISION);
    public static final long GAS_CONSTANT_L_kPa_K = (long) (8.3144598 * PRESCISION);
    public static final long CELSIUS_TO_KELVIN_CONVERSION = (long) (273.15 * PRESCISION);

    public static final int WORLD_WIDTH = 30;
    public static final int SPREAD_RANGE = 3;
    public static final int WORLD_HEIGHT = 30;

}
