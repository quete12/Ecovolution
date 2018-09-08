/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.math;

import processing.core.PGraphics;

/**
 *
 * @author Quentin
 */
public interface Function {

    public BigDouble x(BigDouble y);

    public BigDouble y(BigDouble x);

    public boolean isPositive();

    public boolean isNegative();

    public void limitMinX(BigDouble minX);

    public void limitMaxX(BigDouble maxX);

    public void limitMinY(BigDouble minY);

    public void limitMaxY(BigDouble maxY);

    public boolean isWithinLimits(BigDouble x, BigDouble y);

    default public boolean isPointLeft(BigDouble x, BigDouble y)
    {
        if (!isWithinLimits(x, y))
        {
            return false;
        }
        BigDouble fx = x(y);
        return x.compareTo(fx) < 0;
    }

    default public boolean isPointRight(BigDouble x, BigDouble y)
    {
        if (!isWithinLimits(x, y))
        {
            return false;
        }
        BigDouble fx = x(y);
        return x.compareTo(fx) > 0;
    }

    default public boolean isPointOnOrOver(BigDouble x, BigDouble y)
    {
        if (!isWithinLimits(x, y))
        {
            return false;
        }
        BigDouble fy = y(x);
        return y.compareTo(fy) >= 0;
    }

    default public boolean isPointUnder(BigDouble x, BigDouble y)
    {
        if (!isWithinLimits(x, y))
        {
            return false;
        }
        BigDouble fy = y(x);
        return y.compareTo(fy) < 0;
    }

    public void render(PGraphics g, BigDouble maxYValue, BigDouble maxXValue);
}
