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
public interface Function{

    public double x(double y);

    public double y(double x);

    public boolean isPositive();
    public boolean isNegative();

    default public boolean isPointOnOrLeft(double x, double y)
    {
        return x <= x(y);
    }

    default public boolean isPointLeft(double x, double y)
    {
        return x < x(y);
    }

    default public boolean isPointOnOrRight(double x, double y)
    {
        return x >= x(y);
    }

    default public boolean isPointRight(double x, double y)
    {
        return x > x(y);
    }

    default public boolean isPointOnOrOver(double x, double y)
    {
        return y >= y(x);
    }

    default public boolean isPointOver(double x, double y)
    {
        return y > y(x);
    }

    default public boolean isPointOnOrUnder(double x, double y)
    {
        return y <= y(x);
    }

    default public boolean isPointUnder(double x, double y)
    {
        return y < y(x);
    }
}
