/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.general.math;

import java.math.BigInteger;

/**
 *
 * @author Quentin
 */
public class BigDouble {

    private static final long PRESCISION = 1000000;

    private long value;
    private long fraction;

    public BigDouble()
    {
        this.value = 0;
        this.fraction = 0;
    }

    public BigDouble(double doubleVal)
    {
        this.value = (long) doubleVal;
        this.fraction = (long) ((doubleVal - this.value) * PRESCISION);
        if (value < 0 && fraction > 0)
        {
            fraction *= -1;
        }
    }

    public BigDouble(long value, long fraction)
    {
        this.value = value;
        this.fraction = fraction;
    }

    public BigDouble add(long value, long fraction, BigDouble result)
    {
        long frac = this.fraction + fraction;
        long val = 0;
        if (frac >= PRESCISION)
        {
            frac -= PRESCISION;
            val += 1;
        }
        val += this.value + value;
        result.setFraction(frac);
        result.setValue(val);
        return result;
    }

    public BigDouble add(long value, long fraction)
    {
        return add(value, fraction, this);
    }

    public BigDouble add(BigDouble other)
    {
        return add(other.getValue(), other.getFraction(), this);
    }

    public BigDouble add(BigDouble other, BigDouble result)
    {
        return add(value, fraction, result);
    }

    public BigDouble sub(long value, long fraction, BigDouble result)
    {
        long frac = this.fraction - fraction;
        long val = 0;
        if (frac < 0)
        {
            frac += PRESCISION;
            val -= 1;
        }
        val += this.value - value;
        result.setFraction(frac);
        result.setValue(val);
        return result;
    }

    public BigDouble sub(long value, long fraction)
    {
        return add(value, fraction, this);
    }

    public BigDouble sub(BigDouble other)
    {
        return add(other.getValue(), other.getFraction(), this);
    }

    public BigDouble sub(BigDouble other, BigDouble result)
    {
        return add(value, fraction, result);
    }

    public BigDouble mul(long value, long fraction, BigDouble result)
    {
        long a = this.value * value;
        long b = this.value * fraction;
        long valB = b / PRESCISION;
        long fracB = b - (valB * PRESCISION);
        long c = this.fraction * value;
        long valC = c / PRESCISION;
        long fracC = c - (valC * PRESCISION);
        long d = this.fraction * fraction / PRESCISION;

        System.out.println("a: " + a + " valB: " + valB + " fracB: " + fracB + " valC: " + valC + " fracC: " + fracC + " d: " + d);

        long frac = fracB + fracC + d;
        long val = a + valB + valC;
        if (frac < 0)
        {
            while (frac <= -PRESCISION)
            {
                frac += PRESCISION;
                val -= 1;
            }
        } else
        {
            while (frac >= PRESCISION)
            {
                frac -= PRESCISION;
                val += 1;
            }
        }
        result.setFraction(frac);
        result.setValue(val);
        return result;
    }

    public BigDouble mul(BigDouble other)
    {
        return mul(other.getValue(), other.getFraction(), this);
    }

    public BigDouble div(long value, long fraction, BigDouble result)
    {
        BigInteger bigPrescision = new BigInteger(Long.toString(PRESCISION));
        BigInteger bigOtherValue = new BigInteger(Long.toString(value));
        bigOtherValue = bigOtherValue.multiply(bigPrescision);
        BigInteger bigOtherFraction = new BigInteger(Long.toString(fraction));
        bigOtherValue = bigOtherValue.add(bigOtherFraction);

        BigInteger bigMyValue = new BigInteger(Long.toString(this.value));
        bigMyValue = bigMyValue.multiply(bigPrescision);
        BigInteger bigMyFraction = new BigInteger(Long.toString(this.fraction));
        bigMyValue = bigMyValue.add(bigMyFraction);

        BigInteger bigRes = bigMyValue.multiply(bigPrescision).divide(bigOtherValue);
        long res = bigRes.longValue();
        long val = res / PRESCISION;
        long frac = res - (val * PRESCISION);

        result.setFraction(frac);
        result.setValue(val);
        return result;
    }

    @Deprecated
    public BigDouble olddiv(long value, long fraction, BigDouble result)
    {
        if (value < 0)
        {
            throw new RuntimeException("Dividing by a negative Number!");
        }
        long divval = this.value * PRESCISION;
        long divfrac = this.fraction;
        if (divval < 0 && divfrac > 0)
        {
            divfrac *= -1;
        }
        long dividend = divval + divfrac;
        System.out.println("Dividend before division: " + dividend);
        dividend *= PRESCISION;
        long divisor = (value * PRESCISION) + fraction;
        System.out.println("Dividing: " + Long.toUnsignedString(dividend) + " by " + divisor);
        System.out.println("Max Value: " + Long.toUnsignedString(0-1));
        long res = Long.divideUnsigned(dividend, divisor);
        System.out.println("Res: " + res);
        long val = res / PRESCISION;
        long frac = res - (val * PRESCISION);

        result.setFraction(frac);
        result.setValue(val);
        return result;
    }

    public BigDouble div(BigDouble other)
    {
        return div(other.getValue(), other.getFraction(), this);
    }

    public long getValue()
    {
        return value;
    }

    public void setValue(long value)
    {
        this.value = value;
    }

    public long getFraction()
    {
        return fraction;
    }

    public void setFraction(long fraction)
    {
        this.fraction = fraction;
    }

    @Override
    public String toString()
    {
        return "BigDouble{" + "value=" + value + ", fraction=" + fraction + '}';
    }

}
