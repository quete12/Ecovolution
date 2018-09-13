/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.schaermedia.ecovolution.math;

import java.math.BigInteger;

/**
 *
 * @author Quentin
 */
public class BigDouble implements Comparable<BigDouble> {

    private static final long PRESCISION = 1000000000l;
    private static final String PRESCISION_STRING = Long.toString(PRESCISION);

    public static final BigDouble ONE = new BigDouble(1.0).setImmutable();
    public static final BigDouble NEG_ONE = new BigDouble(-1.0).setImmutable();
    public static final BigDouble ZERO = new BigDouble(0, 0).setImmutable();

    private long value;
    private long fraction;
    private boolean immutable = false;

    public BigDouble()
    {
        this.value = 0;
        this.fraction = 0;
    }

    public BigDouble(double doubleVal)
    {
        this.value = (long) doubleVal;
        this.fraction = Math.round((doubleVal - this.value) * PRESCISION);
        cleanFractionNegation();
    }

    protected void cleanFractionNegation()
    {
        if (value < 0 && fraction > 0)
        {
            fraction *= -1;
        }
        if (value > 0 && fraction < 0)
        {
            fraction *= -1;
        }
    }

    public BigDouble(BigDouble toCopy)
    {
        this.value = toCopy.getValue();
        this.fraction = toCopy.getFraction();
        cleanFractionNegation();
    }

    public BigDouble(long value, long fraction)
    {
        this.value = value;
        this.fraction = fraction;
        cleanFractionNegation();
    }

    public BigDouble setImmutable()
    {
        immutable = true;
        return this;
    }

    public BigDouble add(long value, long fraction, BigDouble result)
    {
        long frac = this.fraction + fraction;
        long val = this.value + value;
        if (frac >= PRESCISION || (frac > 0 && val < 0))
        {
            frac -= PRESCISION;
            val += 1;
        }
        if (frac <= -PRESCISION || (frac < 0 && val > 0))
        {
            frac += PRESCISION;
            val -= 1;
        }
        if (immutable && result == this)
        {
            result = new BigDouble();
        }
        result.setFraction(frac);
        result.setValue(val);
        result.cleanFractionNegation();
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
        return add(other.getValue(), other.getFraction(), result);
    }

    @Override
    public int compareTo(BigDouble o)
    {
        if (this.value > o.getValue())
        {
            return 1;
        } else if (this.value < o.getValue())
        {
            return -1;
        } else if (this.fraction > o.getFraction())
        {
            return 1;
        } else if (this.fraction < o.getFraction())
        {
            return -1;
        } else
        {
            return 0;
        }
    }

    public String toDoubleString()
    {
        String frac;
        String val;
        if (fraction < 0)
        {
            frac = Long.toString(fraction * -1);
            if (value >= 0)
            {
                val = Long.toString(value * -1);
            } else
            {
                val = Long.toString(value);
            }
        } else
        {
            frac = Long.toString(fraction);
            val = Long.toString(value);
        }
        while (frac.length() < PRESCISION_STRING.length() - 1)
        {
            frac = "0" + frac;
        }
        return val + "." + frac;
    }

    public double toDouble()
    {
        return new Double(toDoubleString());
    }

    public BigDouble sub(long value, long fraction, BigDouble result)
    {
        long frac = this.fraction - fraction;
        long val = this.value - value;
        if (frac < -PRESCISION || (val > 0 && frac < 0))
        {
            frac += PRESCISION;
            val -= 1;
        }
        if (frac > PRESCISION || (val < 0 && frac > 0))
        {
            frac -= PRESCISION;
            val += 1;
        }
        if (immutable && result == this)
        {
            result = new BigDouble();
        }
        result.setFraction(frac);
        result.setValue(val);
        result.cleanFractionNegation();
        return result;
    }

    public BigDouble sub(long value, long fraction)
    {
        return sub(value, fraction, this);
    }

    public BigDouble sub(BigDouble other)
    {
        return sub(other.getValue(), other.getFraction(), this);
    }

    public BigDouble sub(BigDouble other, BigDouble result)
    {
        return sub(other.getValue(), other.getFraction(), result);
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

        //System.out.println("a: " + a + " valB: " + valB + " fracB: " + fracB + " valC: " + valC + " fracC: " + fracC + " d: " + d);
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
        if (immutable && result == this)
        {
            result = new BigDouble();
        }
        result.setFraction(frac);
        result.setValue(val);
        result.cleanFractionNegation();
        return result;
    }

    public BigDouble mul(long value, long fraction)
    {
        return mul(value, fraction, this);
    }

    public BigDouble mul(BigDouble other)
    {
        return mul(other.getValue(), other.getFraction(), this);
    }

    public BigDouble mul(BigDouble other, BigDouble result)
    {
        return mul(other.getValue(), other.getFraction(), result);
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
        //System.out.println("Dividing: " + bigMyValue + " by " + bigOtherValue);

        BigInteger bigRes = (bigMyValue.multiply(bigPrescision)).divide(bigOtherValue);
        BigInteger bigVal = bigRes.divide(bigPrescision);
        BigInteger bigFrac = bigRes.subtract(bigVal.multiply(bigPrescision));
        long val = bigVal.longValue();
        long frac = bigFrac.longValue();

        if (immutable && result == this)
        {
            result = new BigDouble();
        }
        result.setFraction(frac);
        result.setValue(val);
        result.cleanFractionNegation();
        return result;
    }

    public BigDouble div(long value, long fraction)
    {
        return div(value, fraction, this);
    }

    public BigDouble div(BigDouble other)
    {
        return div(other.getValue(), other.getFraction(), this);
    }

    public BigDouble div(BigDouble other, BigDouble result)
    {
        return div(other.getValue(), other.getFraction(), result);
    }

    public void limitHigh(BigDouble limit)
    {
        if (compareTo(limit) > 0)
        {
            set(limit);
        }
    }

    public void limitLow(BigDouble limit)
    {
        if (compareTo(limit) < 0)
        {
            set(limit);
        }
    }

    public void set(BigDouble val)
    {
        if (immutable)
        {
            return;
        }
        this.value = val.getValue();
        this.fraction = val.getFraction();
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

    public boolean isPositive()
    {
        return this.value > 0 || (this.value == 0 && this.fraction > 0);
    }

    public boolean isNegative()
    {
        return this.value < 0 || (this.value == 0 && this.fraction < 0);
    }

    public boolean isZero()
    {
        return this.value == 0 && this.fraction == 0;
    }

    public boolean isNotZero()
    {
        return this.value != 0 || this.fraction != 0;
    }

    public void clear()
    {
        this.value = 0;
        this.fraction = 0;
    }

    public static BigDouble max(BigDouble a, BigDouble b)
    {
        int compare = a.compareTo(b);
        if (compare >= 0)
        {
            return a;
        } else
        {
            return b;
        }
    }

    public static BigDouble min(BigDouble a, BigDouble b)
    {
        int compare = a.compareTo(b);
        if (compare <= 0)
        {
            return a;
        } else
        {
            return b;
        }
    }

    public BigDouble abs()
    {
        if (isNegative())
        {
            return mul(NEG_ONE, new BigDouble());
        }
        return new BigDouble(this);
    }

    @Override
    public String toString()
    {
        return "BigDouble{" + "value=" + value + ", fraction=" + fraction + '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof BigDouble))
        {
            return false;
        }
        BigDouble other = (BigDouble) obj;
        if (this.compareTo(other) == 0)
        {
            return true;
        }
        return false;
    }

}
