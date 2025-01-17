/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math.analysis;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.math.FunctionEvaluationException;

/**
 * Represents a polynomial spline function.
 * <p>
 * A <strong>polynomial spline function</strong> consists of a set of <i>interpolating polynomials</i> 
 * and an ascending array of  domain <i>knot points</i>, determining the intervals over which the 
 * spline function is defined by the constituent polynomials.  The polynomials are assumed to have 
 * been computed to match the values of another function at the knot points.  The value 
 * consistency constraints are not currently enforced by <code>PolynomialSplineFunction</code> itself,
 * but are assumed to hold  among the polynomials and knot points passed to the constructor.
 * <p>
 * N.B.:  The polynomials in the <code>polynomials</code> property must be centered on the knot points
 * to compute the spline function values.  See below.
 * <p>
 * The value of the polynomial spline function for an argument <code>x</code> is computed as follows:
 * <ol>
 * <li>The knot array is searched to find the segment to which <code>x</code> belongs.  
 *  If <code>x</code> is less than the smallest knot point or greater than or equal to the largest one, an 
 *  <code>IllegalArgumentException</code> is thrown.</li>
 * <li> Let <code>j</code> be the index of the largest knot point that is less than or equal to <code>x</code>. 
 *  The value returned is <br> <code>polynomials[j](x - knot[j])</code></li></ol>
 *
 * @version $Revision: 1.7 $ $Date: 2004/07/17 21:19:39 $
 */
public class PolynomialSplineFunction implements UnivariateRealFunction, Serializable {
   
    /** Serializable version identifier */
    static final long serialVersionUID = 7011031166416885789L;
    
    /** Spline segment interval delimiters (knots).   Size is n+1 for n segments. */
    private double knots[];

    /**
     * The polynomial functions that make up the spline.  The first element determines the value of the spline
     * over the first subinterval, the second over the second, etc.   Spline function values are determined by
     * evaluating these functions at <code>(x - knot[i])</code> where i is the knot segment to which x belongs.
     */
    private PolynomialFunction polynomials[] = null;
    
    /** Number of spline segments = number of polynomials = number of partition points - 1 */
    private int n = 0;
    

    /**
     * Construct a polynomial spline function with the given segment delimiters and interpolating
     * polynomials.
     * <p>
     * The constructor copies both arrays and assigns the copies to the knots and polynomials properties,
     * respectively.
     * 
     * @param knots spline segment interval delimiters
     * @param polynomials polynomial functions that make up the spline
     * @throws NullPointerException if either of the input arrays is null
     * @throws IllegalArgumentException if knots has length less than 2,  
     *     <code>polynomials.length != knots.length - 1 </code>, or the knots array
     *     is not strictly increasing.
     * 
     */
    public PolynomialSplineFunction(double knots[], PolynomialFunction polynomials[]) {
        super();
        if (knots.length < 2) {
            throw new IllegalArgumentException
            	("Not enough knot values -- spline partition must have at least 2 points.");
        }
        if (knots.length - 1 != polynomials.length) {
            throw new IllegalArgumentException 
            ("Number of polynomial interpolants must match the number of segments.");
        }
        if (!isStrictlyIncreasing(knots)) {
            throw new IllegalArgumentException 
                ("Knot values must be strictly increasing.");
        }
        
        this.n = knots.length -1;
        this.knots = new double[n + 1];
        System.arraycopy(knots, 0, this.knots, 0, n + 1);
        this.polynomials = new PolynomialFunction[n];
        System.arraycopy(polynomials, 0, this.polynomials, 0, n);
    }

    /**
     * Compute the value for the function.
     * 
     * @param v the point for which the function value should be computed
     * @return the value
     * @throws FunctionEvaluationException if v is outside of the domain of
     * of the spline function (less than the smallest knot point or greater
     * than the largest knot point)
     */
    public double value(double v) throws FunctionEvaluationException {
        if (v < knots[0] || v >= knots[n]) {
            throw new FunctionEvaluationException(v,"Argument outside domain");
        }
        int i = Arrays.binarySearch(knots, v);
        if (i < 0) {
            i = -i - 2;
        }
        return polynomials[i].value(v - knots[i]);
    }
    
    /**
     * Returns the derivative of the polynomial spline function as a UnivariateRealFunction
     * @return  the derivative function
     */
    public UnivariateRealFunction derivative() {
        return polynomialSplineDerivative();
    }
    
    /**
     * Returns the derivative of the polynomial spline function as a PolynomialSplineFunction
     * 
     * @return  the derivative function
     */
    public PolynomialSplineFunction polynomialSplineDerivative() {
        PolynomialFunction derivativePolynomials[] = new PolynomialFunction[n];
        for (int i = 0; i < n; i++) {
            derivativePolynomials[i] = polynomials[i].polynomialDerivative();
        }
        return new PolynomialSplineFunction(knots, derivativePolynomials);
    }

    /**
     * Returns the number of spline segments = the number of polynomials = the number of knot points - 1.
     * 
     * @return the number of spline segments
     */
    public int getN() {
        return n;
    }

    /**
     * Returns a copy of the interpolating polynomials array.
     * <p>
     * Returns a fresh copy of the array. Changes made to the copy will
     * not affect the polynomials property.
     * 
     * @return the interpolating polynomials
     */
    public PolynomialFunction[] getPolynomials() {
        PolynomialFunction p[] = new PolynomialFunction[n];
        System.arraycopy(polynomials, 0, p, 0, n);
        return p;
    }

    /**
     * Returns an array copy of the knot points.
     * <p>
     * Returns a fresh copy of the array. Changes made to the copy
     * will not affect the knots property.
     * 
     * @return the knot points
     */
    public double[] getKnots() {
        double out[] = new double[n + 1];
        System.arraycopy(knots, 0, out, 0, n + 1);
        return out;  
    }

    /**
     * Determines if the given array is ordered in a strictly increasing
     * fashion.
     * @param x the array to examine.
     * @return <code>true</code> if the elements in <code>x</code> are ordered
     *         in a stricly increasing manner.  <code>false</code>, otherwise.
     */
    private static boolean isStrictlyIncreasing(double[] x) {
        for (int i = 1; i < x.length; ++i) {
            if (x[i - 1] >= x[i]) {
                return false;
            }
        }
        return true;
    }
}
