/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.fraction;

import java.math.BigInteger;
import org.apache.commons.math.util.MathUtils;

/**
 * Representation of a rational number.
 *
 * @since 1.1
 * @version $Revision$ $Date$
 */
public class Fraction extends Number implements Comparable {

    /** A fraction representing "1 / 1". */
    public static final Fraction ONE = new Fraction(1, 1);

    /** A fraction representing "0 / 1". */
    public static final Fraction ZERO = new Fraction(0, 1);

    /**
     * The maximal number of denominator digits that can be requested for double to fraction
     * conversion.
     * <p>
     * When <code>d</code> digits are requested, an integer threshold is
     * initialized with the value 10<sup>d</sup>. Therefore, <code>d</code>
     * cannot be larger than this constant. Since the java language uses 32 bits
     * signed integers, the value for this constant is 9.
     * </p>
     * 
     * @see #Fraction(double,int)
     */
    public static final int MAX_DENOMINATOR_DIGITS = 9;
    
    /** Serializable version identifier */
    private static final long serialVersionUID = 5463066929751300926L;
    
    /** The denominator. */
    private int denominator;
    
    /** The numerator. */
    private int numerator;

    /**
     * Create a fraction given the double value.
     * @param value the double value to convert to a fraction.
     * @throws FractionConversionException if the continued fraction failed to
     *         converge.
     */
    public Fraction(double value) throws FractionConversionException {
        this(value, 1.0e-5, 100);
    }

    /**
     * Create a fraction given the double value and maximum error allowed.
     * <p>
     * References:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html">
     * Continued Fraction</a> equations (11) and (22)-(26)</li>
     * </ul>
     * </p>
     * @param value the double value to convert to a fraction.
     * @param epsilon maximum error allowed.  The resulting fraction is within
     *        <code>epsilon</code> of <code>value</code>, in absolute terms.
     * @param maxIterations maximum number of convergents
     * @throws FractionConversionException if the continued fraction failed to
     *         converge.
     */
    public Fraction(double value, double epsilon, int maxIterations)
        throws FractionConversionException
    {
        this(value, epsilon, Integer.MAX_VALUE, maxIterations);
    }

    /**
     * Convert a number of denominator digits to a denominator max value.
     * @param denominatorDigits The maximum number of denominator digits.
     * @return the maximal value for denominator
     * @throws IllegalArgumentException if more than {@link #MAX_DENOMINATOR_DIGITS}
     *         are requested
     */
    private static int maxDenominator(int denominatorDigits)
        throws IllegalArgumentException
    {
        if (denominatorDigits > MAX_DENOMINATOR_DIGITS) {
            throw new IllegalArgumentException("too many digits requested");
        }
        return (int)Math.pow(10, denominatorDigits);
    }

    /**
     * Create a fraction given the double value and maximum number of
     * denominator digits.
     * <p>
     * References:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html">
     * Continued Fraction</a> equations (11) and (22)-(26)</li>
     * </ul>
     * </p>
     * @param value the double value to convert to a fraction.
     * @param denominatorDigits The maximum number of denominator digits.
     * @throws FractionConversionException if the continued fraction failed to
     *         converge
     * @throws IllegalArgumentException if more than {@link #MAX_DENOMINATOR_DIGITS}
     *         are requested
     */
    public Fraction(double value, int denominatorDigits)
        throws FractionConversionException, IllegalArgumentException
    {
       this(value, 0, maxDenominator(denominatorDigits), 100);
    }

    /**
     * Create a fraction given the double value and either the maximum error
     * allowed or the maximum number of denominator digits.
     * <p>
     *
     * NOTE: This constructor is called with EITHER
     *   - a valid epsilon value and the maxDenominator set to Integer.MAX_VALUE
     *     (that way the maxDenominator has no effect).
     * OR
     *   - a valid maxDenominator value and the epsilon value set to zero
     *     (that way epsilon only has effect if there is an exact match before
     *     the maxDenominator value is reached).
     * <p>
     *
     * It has been done this way so that the same code can be (re)used for both
     * scenarios. However this could be confusing to users if it were part of
     * the public API and this constructor should therefore remain PRIVATE.
     * </p>
     *
     * See JIRA issue ticket MATH-181 for more details:
     *
     *     https://issues.apache.org/jira/browse/MATH-181
     *
     * @param value the double value to convert to a fraction.
     * @param epsilon maximum error allowed.  The resulting fraction is within
     *        <code>epsilon</code> of <code>value</code>, in absolute terms.
     * @param maxDenominator maximum denominator value allowed.
     * @param maxIterations maximum number of convergents
     * @throws FractionConversionException if the continued fraction failed to
     *         converge.
     */
    private Fraction(double value, double epsilon, int maxDenominator, int maxIterations)
        throws FractionConversionException
    {
        double r0 = value;
        int a0 = (int)Math.floor(r0);
        if (Math.abs(a0) > Integer.MAX_VALUE) {
            throw new FractionConversionException(value, a0);
        }

        // check for (almost) integer arguments, which should not go
        // to iterations.
        if (Math.abs(a0 - value) < epsilon) {
            this.numerator = a0;
            this.denominator = 1;
            return;
        }
        
        int p0 = 1;
        int q0 = 0;
        int p1 = a0;
        int q1 = 1;

        int p2 = 0;
        int q2 = 1;

        int n = 0;
        boolean stop = false;
        do {
            ++n;
            double r1 = 1.0 / (r0 - a0);
            int a1 = (int)Math.floor(r1);
            p2 = (a1 * p1) + p0;
            q2 = (a1 * q1) + q0;
            if ((Math.abs(p2) > Integer.MAX_VALUE) || (Math.abs(q2) > Integer.MAX_VALUE)) {
                throw new FractionConversionException(p2, q2);
            }

            double convergent = (double)p2 / (double)q2;
            if (n < maxIterations && Math.abs(convergent - value) > epsilon && q2 < maxDenominator) {
                p0 = p1;
                p1 = p2;
                q0 = q1;
                q1 = q2;
                a0 = a1;
                r0 = r1;
            } else {
                stop = true;
            }
        } while (!stop);

        if (n >= maxIterations) {
            throw new FractionConversionException(value, maxIterations);
        }
        
        if (q2 < maxDenominator) {
            this.numerator = p2;
            this.denominator = q2;
        } else {
            this.numerator = p1;
            this.denominator = q1;
        }
        reduce();
    }
    
    /**
     * Create a fraction given the numerator and denominator.  The fraction is
     * reduced to lowest terms.
     * @param num the numerator.
     * @param den the denominator.
     * @throws ArithmeticException if the denomiator is <code>zero</code>
     */
    public Fraction(int num, int den) {
        super();
        if (den == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        }
        if (den < 0) {
            if (num == Integer.MIN_VALUE ||
                    den == Integer.MIN_VALUE) {
                throw new ArithmeticException("overflow: can't negate");
            }
            num = -num;
            den = -den;
        }
        this.numerator = num;
        this.denominator = den;
        reduce();
    }
    
    /**
     * Returns the absolute value of this fraction.
     * @return the absolute value.
     */
    public Fraction abs() {
        Fraction ret;
        if (numerator >= 0) {
            ret = this;
        } else {
            ret = negate();
        }
        return ret;        
    }
    
    /**
     * Compares this object to another based on size.
     * @param object the object to compare to
     * @return -1 if this is less than <tt>object</tt>, +1 if this is greater
     *         than <tt>object</tt>, 0 if they are equal.
     */
    public int compareTo(Object object) {
        int ret = 0;
        
        if (this != object) { 
            Fraction other = (Fraction)object;
            double first = doubleValue();
            double second = other.doubleValue();
            
            if (first < second) {
                ret = -1;
            } else if (first > second) {
                ret = 1;
            }
        }
        
        return ret;
    }
    
    /**
     * Gets the fraction as a <tt>double</tt>. This calculates the fraction as
     * the numerator divided by denominator.
     * @return the fraction as a <tt>double</tt>
     */
    public double doubleValue() {
        return (double)numerator / (double)denominator;
    }
    
    /**
     * Test for the equality of two fractions.  If the lowest term
     * numerator and denominators are the same for both fractions, the two
     * fractions are considered to be equal.
     * @param other fraction to test for equality to this fraction
     * @return true if two fractions are equal, false if object is
     *         <tt>null</tt>, not an instance of {@link Fraction}, or not equal
     *         to this fraction instance.
     */
    public boolean equals(Object other) {
        boolean ret;
        
        if (this == other) { 
            ret = true;
        } else if (other == null) {
            ret = false;
        } else {
            try {
                // since fractions are always in lowest terms, numerators and
                // denominators can be compared directly for equality.
                Fraction rhs = (Fraction)other;
                ret = (numerator == rhs.numerator) &&
                    (denominator == rhs.denominator);
            } catch (ClassCastException ex) {
                // ignore exception
                ret = false;
            }
        }
        
        return ret;
    }
    
    /**
     * Gets the fraction as a <tt>float</tt>. This calculates the fraction as
     * the numerator divided by denominator.
     * @return the fraction as a <tt>float</tt>
     */
    public float floatValue() {
        return (float)doubleValue();
    }
    
    /**
     * Access the denominator.
     * @return the denominator.
     */
    public int getDenominator() {
        return denominator;
    }
    
    /**
     * Access the numerator.
     * @return the numerator.
     */
    public int getNumerator() {
        return numerator;
    }
    
    /**
     * Gets a hashCode for the fraction.
     * @return a hash code value for this object
     */
    public int hashCode() {
        return 37 * (37 * 17 + getNumerator()) + getDenominator();
    }
    
    /**
     * Gets the fraction as an <tt>int</tt>. This returns the whole number part
     * of the fraction.
     * @return the whole number fraction part
     */
    public int intValue() {
        return (int)doubleValue();
    }
    
    /**
     * Gets the fraction as a <tt>long</tt>. This returns the whole number part
     * of the fraction.
     * @return the whole number fraction part
     */
    public long longValue() {
        return (long)doubleValue();
    }
    
    /**
     * Return the additive inverse of this fraction.
     * @return the negation of this fraction.
     */
    public Fraction negate() {
        if (numerator==Integer.MIN_VALUE) {
            throw new ArithmeticException("overflow: too large to negate");
        }
        return new Fraction(-numerator, denominator);
    }

    /**
     * Return the multiplicative inverse of this fraction.
     * @return the reciprocal fraction
     */
    public Fraction reciprocal() {
        return new Fraction(denominator, numerator);
    }
    
    /**
     * <p>Adds the value of this fraction to another, returning the result in reduced form.
     * The algorithm follows Knuth, 4.5.1.</p>
     *
     * @param fraction  the fraction to add, must not be <code>null</code>
     * @return a <code>Fraction</code> instance with the resulting values
     * @throws IllegalArgumentException if the fraction is <code>null</code>
     * @throws ArithmeticException if the resulting numerator or denominator exceeds
     *  <code>Integer.MAX_VALUE</code>
     */
    public Fraction add(Fraction fraction) {
        return addSub(fraction, true /* add */);
    }

    /**
     * <p>Subtracts the value of another fraction from the value of this one, 
     * returning the result in reduced form.</p>
     *
     * @param fraction  the fraction to subtract, must not be <code>null</code>
     * @return a <code>Fraction</code> instance with the resulting values
     * @throws IllegalArgumentException if the fraction is <code>null</code>
     * @throws ArithmeticException if the resulting numerator or denominator
     *   cannot be represented in an <code>int</code>.
     */
    public Fraction subtract(Fraction fraction) {
        return addSub(fraction, false /* subtract */);
    }

    /** 
     * Implement add and subtract using algorithm described in Knuth 4.5.1.
     * 
     * @param fraction the fraction to subtract, must not be <code>null</code>
     * @param isAdd true to add, false to subtract
     * @return a <code>Fraction</code> instance with the resulting values
     * @throws IllegalArgumentException if the fraction is <code>null</code>
     * @throws ArithmeticException if the resulting numerator or denominator
     *   cannot be represented in an <code>int</code>.
     */
    private Fraction addSub(Fraction fraction, boolean isAdd) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        // zero is identity for addition.
        if (numerator == 0) {
            return isAdd ? fraction : fraction.negate();
        }
        if (fraction.numerator == 0) {
            return this;
        }     
        // if denominators are randomly distributed, d1 will be 1 about 61%
        // of the time.
        int d1 = MathUtils.gcd(denominator, fraction.denominator);
        if (d1==1) {
            // result is ( (u*v' +/- u'v) / u'v')
            int uvp = MathUtils.mulAndCheck(numerator, fraction.denominator);
            int upv = MathUtils.mulAndCheck(fraction.numerator, denominator);
            return new Fraction
                (isAdd ? MathUtils.addAndCheck(uvp, upv) : 
                 MathUtils.subAndCheck(uvp, upv),
                 MathUtils.mulAndCheck(denominator, fraction.denominator));
        }
        // the quantity 't' requires 65 bits of precision; see knuth 4.5.1
        // exercise 7.  we're going to use a BigInteger.
        // t = u(v'/d1) +/- v(u'/d1)
        BigInteger uvp = BigInteger.valueOf(numerator)
        .multiply(BigInteger.valueOf(fraction.denominator/d1));
        BigInteger upv = BigInteger.valueOf(fraction.numerator)
        .multiply(BigInteger.valueOf(denominator/d1));
        BigInteger t = isAdd ? uvp.add(upv) : uvp.subtract(upv);
        // but d2 doesn't need extra precision because
        // d2 = gcd(t,d1) = gcd(t mod d1, d1)
        int tmodd1 = t.mod(BigInteger.valueOf(d1)).intValue();
        int d2 = (tmodd1==0)?d1:MathUtils.gcd(tmodd1, d1);

        // result is (t/d2) / (u'/d1)(v'/d2)
        BigInteger w = t.divide(BigInteger.valueOf(d2));
        if (w.bitLength() > 31) {
            throw new ArithmeticException
            ("overflow: numerator too large after multiply");
        }
        return new Fraction (w.intValue(), 
                MathUtils.mulAndCheck(denominator/d1, 
                        fraction.denominator/d2));
    }

    /**
     * <p>Multiplies the value of this fraction by another, returning the 
     * result in reduced form.</p>
     *
     * @param fraction  the fraction to multiply by, must not be <code>null</code>
     * @return a <code>Fraction</code> instance with the resulting values
     * @throws IllegalArgumentException if the fraction is <code>null</code>
     * @throws ArithmeticException if the resulting numerator or denominator exceeds
     *  <code>Integer.MAX_VALUE</code>
     */
    public Fraction multiply(Fraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        if (numerator == 0 || fraction.numerator == 0) {
            return ZERO;
        }
        // knuth 4.5.1
        // make sure we don't overflow unless the result *must* overflow.
        int d1 = MathUtils.gcd(numerator, fraction.denominator);
        int d2 = MathUtils.gcd(fraction.numerator, denominator);
        return getReducedFraction
        (MathUtils.mulAndCheck(numerator/d1, fraction.numerator/d2),
                MathUtils.mulAndCheck(denominator/d2, fraction.denominator/d1));
    }

    /**
     * <p>Divide the value of this fraction by another.</p>
     *
     * @param fraction  the fraction to divide by, must not be <code>null</code>
     * @return a <code>Fraction</code> instance with the resulting values
     * @throws IllegalArgumentException if the fraction is <code>null</code>
     * @throws ArithmeticException if the fraction to divide by is zero
     * @throws ArithmeticException if the resulting numerator or denominator exceeds
     *  <code>Integer.MAX_VALUE</code>
     */
    public Fraction divide(Fraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("The fraction must not be null");
        }
        if (fraction.numerator == 0) {
            throw new ArithmeticException("The fraction to divide by must not be zero");
        }
        return multiply(fraction.reciprocal());
    }
    
    /**
     * <p>Creates a <code>Fraction</code> instance with the 2 parts
     * of a fraction Y/Z.</p>
     *
     * <p>Any negative signs are resolved to be on the numerator.</p>
     *
     * @param numerator  the numerator, for example the three in 'three sevenths'
     * @param denominator  the denominator, for example the seven in 'three sevenths'
     * @return a new fraction instance, with the numerator and denominator reduced
     * @throws ArithmeticException if the denominator is <code>zero</code>
     */
    public static Fraction getReducedFraction(int numerator, int denominator) {
        if (denominator == 0) {
            throw new ArithmeticException("The denominator must not be zero");
        }
        if (numerator==0) {
            return ZERO; // normalize zero.
        }
        // allow 2^k/-2^31 as a valid fraction (where k>0)
        if (denominator==Integer.MIN_VALUE && (numerator&1)==0) {
            numerator/=2; denominator/=2;
        }
        if (denominator < 0) {
            if (numerator==Integer.MIN_VALUE ||
                    denominator==Integer.MIN_VALUE) {
                throw new ArithmeticException("overflow: can't negate");
            }
            numerator = -numerator;
            denominator = -denominator;
        }
        // simplify fraction.
        int gcd = MathUtils.gcd(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;
        return new Fraction(numerator, denominator);
    }
    
    /**
     * Reduce this fraction to lowest terms.  This is accomplished by dividing
     * both numerator and denominator by their greatest common divisor.
     */
    private void reduce() {
        // reduce numerator and denominator by greatest common denominator.
        int d = MathUtils.gcd(numerator, denominator);
        if (d > 1) {
            numerator /= d;
            denominator /= d;
        }

        // move sign to numerator.
        if (denominator < 0) {
            numerator *= -1;
            denominator *= -1;
        }
    }
}
