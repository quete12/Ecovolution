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
public interface Function {

    public BigDouble x(BigDouble y);

    public BigDouble y(BigDouble x);

    public boolean isPositive();

    public boolean isNegative();

    default public boolean isPointLeft(BigDouble x, BigDouble y)
    {
        BigDouble fx = x(y);
        return x.compareTo(fx) < 0;
    }

    default public boolean isPointRight(BigDouble x, BigDouble y)
    {
        BigDouble fx = x(y);
        return x.compareTo(fx) > 0;
    }

    default public boolean isPointOnOrOver(BigDouble x, BigDouble y)
    {
        BigDouble fy = y(x);
        return y.compareTo(fy) >= 0;
    }

    default public boolean isPointUnder(BigDouble x, BigDouble y)
    {
        BigDouble fy = y(x);
        return y.compareTo(fy) < 0;
    }
}
