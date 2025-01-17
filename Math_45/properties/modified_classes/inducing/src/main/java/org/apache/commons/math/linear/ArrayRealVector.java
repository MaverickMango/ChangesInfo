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
package org.apache.commons.math.linear;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.MathArithmeticException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.FastMath;

/**
 * This class implements the {@link RealVector} interface with a double array.
 * @version $Revision$ $Date$
 * @since 2.0
 */
public class ArrayRealVector extends AbstractRealVector implements Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = -1097961340710804027L;
    /** Default format. */
    private static final RealVectorFormat DEFAULT_FORMAT =
        RealVectorFormat.getInstance();
    /** Entries of the vector. */
    protected double data[];

    /**
     * Build a 0-length vector.
     * Zero-length vectors may be used to initialized construction of vectors
     * by data gathering. We start with zero-length and use either the {@link
     * #ArrayRealVector(ArrayRealVector, ArrayRealVector)} constructor
     * or one of the {@code append} method ({@link #append(double)}, {@link
     * #append(double[])}, {@link #append(ArrayRealVector)}) to gather data
     * into this vector.
     */
    public ArrayRealVector() {
        data = new double[0];
    }

    /**
     * Construct a vector of zeroes.
     *
     * @param size Size of the vector.
     */
    public ArrayRealVector(int size) {
        data = new double[size];
    }

    /**
     * Construct a vector with preset values.
     *
     * @param size Size of the vector
     * @param preset All entries will be set with this value.
     */
    public ArrayRealVector(int size, double preset) {
        data = new double[size];
        Arrays.fill(data, preset);
    }

    /**
     * Construct a vector from an array, copying the input array.
     *
     * @param d Array.
     * @throws NullArgumentException if {@code d} is {@code null}.
     */
    public ArrayRealVector(double[] d) {
        data = d.clone();
    }

    /**
     * Create a new ArrayRealVector using the input array as the underlying
     * data array.
     * If an array is built specially in order to be embedded in a
     * ArrayRealVector and not used directly, the {@code copyArray} may be
     * set to {@code false}. This will prevent the copying and improve
     * performance as no new array will be built and no data will be copied.
     *
     * @param d Data for the new vector.
     * @param copyArray if {@code true}, the input array will be copied,
     * otherwise it will be referenced.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @see #ArrayRealVector(double[])
     */
    public ArrayRealVector(double[] d, boolean copyArray) {
        if (d == null) {
            throw new NullArgumentException();
        }
        data = copyArray ? d.clone() :  d;
    }

    /**
     * Construct a vector from part of a array.
     *
     * @param d Array.
     * @param pos Position of first entry.
     * @param size Number of entries to copy.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws NumberIsTooLargeException if the size of {@code d} is less
     * than {@code pos + size}.
     */
    public ArrayRealVector(double[] d, int pos, int size) {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        data = new double[size];
        System.arraycopy(d, pos, data, 0, size);
    }

    /**
     * Construct a vector from an array.
     * @param d array of Doubles.
     */
    public ArrayRealVector(Double[] d) {
        data = new double[d.length];
        for (int i = 0; i < d.length; i++) {
            data[i] = d[i].doubleValue();
        }
    }

    /**
     * Construct a vector from part of an array.
     *
     * @param d Array.
     * @param pos Position of first entry.
     * @param size Number of entries to copy.
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws NumberIsTooLargeException if the size of {@code d} is less
     * than {@code pos + size}.
     */
    public ArrayRealVector(Double[] d, int pos, int size) {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        data = new double[size];
        for (int i = pos; i < pos + size; i++) {
            data[i - pos] = d[i].doubleValue();
        }
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     *
     * @param v vector to copy.
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    public ArrayRealVector(RealVector v) {
        if (v == null) {
            throw new NullArgumentException();
        }
        data = new double[v.getDimension()];
        for (int i = 0; i < data.length; ++i) {
            data[i] = v.getEntry(i);
        }
    }

    /**
     * Construct a vector from another vector, using a deep copy.
     *
     * @param v Vector to copy.
     * @throws NullArgumentException if {@code v} is {@code null}.
     */
    public ArrayRealVector(ArrayRealVector v) {
        this(v, true);
    }

    /**
     * Construct a vector from another vector.
     *
     * @param v Vector to copy.
     * @param deep If {@code true} perform a deep copy, otherwise perform a
     * shallow copy.
     */
    public ArrayRealVector(ArrayRealVector v, boolean deep) {
        data = deep ? v.data.clone() : v.data;
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayRealVector(ArrayRealVector v1, ArrayRealVector v2) {
        data = new double[v1.data.length + v2.data.length];
        System.arraycopy(v1.data, 0, data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, data, v1.data.length, v2.data.length);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayRealVector(ArrayRealVector v1, RealVector v2) {
        final int l1 = v1.data.length;
        final int l2 = v2.getDimension();
        data = new double[l1 + l2];
        System.arraycopy(v1.data, 0, data, 0, l1);
        for (int i = 0; i < l2; ++i) {
            data[l1 + i] = v2.getEntry(i);
        }
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayRealVector(RealVector v1, ArrayRealVector v2) {
        final int l1 = v1.getDimension();
        final int l2 = v2.data.length;
        data = new double[l1 + l2];
        for (int i = 0; i < l1; ++i) {
            data[i] = v1.getEntry(i);
        }
        System.arraycopy(v2.data, 0, data, l1, l2);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayRealVector(ArrayRealVector v1, double[] v2) {
        final int l1 = v1.getDimension();
        final int l2 = v2.length;
        data = new double[l1 + l2];
        System.arraycopy(v1.data, 0, data, 0, l1);
        System.arraycopy(v2, 0, data, l1, l2);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayRealVector(double[] v1, ArrayRealVector v2) {
        final int l1 = v1.length;
        final int l2 = v2.getDimension();
        data = new double[l1 + l2];
        System.arraycopy(v1, 0, data, 0, l1);
        System.arraycopy(v2.data, 0, data, l1, l2);
    }

    /**
     * Construct a vector by appending one vector to another vector.
     * @param v1 first vector (will be put in front of the new vector)
     * @param v2 second vector (will be put at back of the new vector)
     */
    public ArrayRealVector(double[] v1, double[] v2) {
        final int l1 = v1.length;
        final int l2 = v2.length;
        data = new double[l1 + l2];
        System.arraycopy(v1, 0, data, 0, l1);
        System.arraycopy(v2, 0, data, l1, l2);
    }

    /** {@inheritDoc} */
    @Override
    public AbstractRealVector copy() {
        return new ArrayRealVector(this, true);
    }

    /** {@inheritDoc} */
    @Override
    public RealVector add(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return add((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double[] out = data.clone();
            Iterator<Entry> it = v.sparseIterator();
            Entry e;
            while (it.hasNext() && (e = it.next()) != null) {
                out[e.getIndex()] += e.getValue();
            }
            return new ArrayRealVector(out, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RealVector add(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double[] out = data.clone();
        for (int i = 0; i < data.length; i++) {
            out[i] += v[i];
        }
        return new ArrayRealVector(out, false);
    }

    /**
     * Compute the sum of this and v.
     * @param v vector to be added
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayRealVector add(ArrayRealVector v)
        throws IllegalArgumentException {
        return (ArrayRealVector) add(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public RealVector subtract(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return subtract((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double[] out = data.clone();
            Iterator<Entry> it = v.sparseIterator();
            Entry e;
            while(it.hasNext() && (e = it.next()) != null) {
                out[e.getIndex()] -= e.getValue();
            }
            return new ArrayRealVector(out, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RealVector subtract(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double[] out = data.clone();
        for (int i = 0; i < data.length; i++) {
            out[i] -= v[i];
        }
        return new ArrayRealVector(out, false);
    }

    /**
     * Compute this minus v.
     * @param v vector to be subtracted
     * @return this + v
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayRealVector subtract(ArrayRealVector v)
        throws IllegalArgumentException {
        return (ArrayRealVector) subtract(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapAddToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] + d;
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapSubtractToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] - d;
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapMultiplyToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] * d;
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapDivideToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] / d;
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapPowToSelf(double d) {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.pow(data[i], d);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapExpToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.exp(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapExpm1ToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.expm1(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapLogToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.log(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapLog10ToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.log10(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapLog1pToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.log1p(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapCoshToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.cosh(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapSinhToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.sinh(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapTanhToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.tanh(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapCosToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.cos(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapSinToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.sin(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapTanToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.tan(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapAcosToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.acos(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapAsinToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.asin(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapAtanToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.atan(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapInvToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 1.0 / data[i];
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapAbsToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.abs(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapSqrtToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.sqrt(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapCbrtToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.cbrt(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapCeilToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.ceil(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapFloorToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.floor(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapRintToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.rint(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapSignumToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.signum(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public RealVector mapUlpToSelf() {
        for (int i = 0; i < data.length; i++) {
            data[i] = FastMath.ulp(data[i]);
        }
        return this;
    }

    /** {@inheritDoc} */
    public RealVector ebeMultiply(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return ebeMultiply((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double[] out = data.clone();
            for (int i = 0; i < data.length; i++) {
                out[i] *= v.getEntry(i);
            }
            return new ArrayRealVector(out, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RealVector ebeMultiply(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double[] out = data.clone();
        for (int i = 0; i < data.length; i++) {
            out[i] *= v[i];
        }
        return new ArrayRealVector(out, false);
    }

    /**
     * Element-by-element multiplication.
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing this[i] * v[i] for all i
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public ArrayRealVector ebeMultiply(ArrayRealVector v)
        throws IllegalArgumentException {
        return (ArrayRealVector) ebeMultiply(v.data);
    }

    /** {@inheritDoc} */
    public RealVector ebeDivide(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return ebeDivide((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double[] out = data.clone();
            for (int i = 0; i < data.length; i++) {
                out[i] /= v.getEntry(i);
            }
            return new ArrayRealVector(out, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public RealVector ebeDivide(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double[] out = data.clone();
        for (int i = 0; i < data.length; i++) {
                out[i] /= v[i];
        }
        return new ArrayRealVector(out, false);
    }

    /**
     * Element-by-element division.
     * @param v vector by which instance elements must be divided
     * @return a vector containing this[i] / v[i] for all i
     * @throws IllegalArgumentException if v is not the same size as this
     */
    public ArrayRealVector ebeDivide(ArrayRealVector v)
        throws IllegalArgumentException {
        return (ArrayRealVector) ebeDivide(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public double[] getData() {
        return data.clone();
    }

    /**
     * Returns a reference to the underlying data array.
     * <p>Does not make a fresh copy of the underlying data.</p>
     * @return array of entries
     */
    public double[] getDataRef() {
        return data;
    }

    /** {@inheritDoc} */
    @Override
    public double dotProduct(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return dotProduct((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double dot = 0;
            Iterator<Entry> it = v.sparseIterator();
            Entry e;
            while(it.hasNext() && (e = it.next()) != null) {
                dot += data[e.getIndex()] * e.getValue();
            }
            return dot;
        }
    }

    /** {@inheritDoc} */
    @Override
    public double dotProduct(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double dot = 0;
        for (int i = 0; i < data.length; i++) {
            dot += data[i] * v[i];
        }
        return dot;
    }

    /**
     * Compute the dot product.
     *
     * @param v Vector with which dot product should be computed
     * @return the scalar dot product between instance and {@code v}.
     * @throws DimensionMismatchException if {@code v} is not the same
     * size as this vector.
     */
    public double dotProduct(ArrayRealVector v) {
        return dotProduct(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public double getNorm() {
        double sum = 0;
        for (double a : data) {
            sum += a * a;
        }
        return FastMath.sqrt(sum);
    }

    /** {@inheritDoc} */
    @Override
    public double getL1Norm() {
        double sum = 0;
        for (double a : data) {
            sum += FastMath.abs(a);
        }
        return sum;
    }

    /** {@inheritDoc} */
    @Override
    public double getLInfNorm() {
        double max = 0;
        for (double a : data) {
            max = FastMath.max(max, FastMath.abs(a));
        }
        return max;
    }

    /** {@inheritDoc} */
    @Override
    public double getDistance(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return getDistance((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i);
                sum += delta * delta;
            }
            return FastMath.sqrt(sum);
        }
    }

    /** {@inheritDoc} */
    @Override
    public double getDistance(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double sum = 0;
        for (int i = 0; i < data.length; ++i) {
            final double delta = data[i] - v[i];
            sum += delta * delta;
        }
        return FastMath.sqrt(sum);
    }

   /**
     * Distance between two vectors.
     * This method computes the distance consistent with the
     * L<sub>2</sub> norm, i.e. the square root of the sum of
     * elements differences, or euclidian distance.
     *
     * @param v vector to which distance is requested
     * @return the distance between two vectors.
     * @throws DimensionMismatchException if {@code v} is not the same size as
     * this vector.
     * @see #getDistance(RealVector)
     * @see #getL1Distance(ArrayRealVector)
     * @see #getLInfDistance(ArrayRealVector)
     * @see #getNorm()
     */
    public double getDistance(ArrayRealVector v) {
        return getDistance(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public double getL1Distance(RealVector v) {
        if (v instanceof ArrayRealVector) {
            return getL1Distance((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double sum = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i);
                sum += FastMath.abs(delta);
            }
            return sum;
        }
    }

    /** {@inheritDoc} */
    @Override
    public double getL1Distance(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double sum = 0;
        for (int i = 0; i < data.length; ++i) {
            final double delta = data[i] - v[i];
            sum += FastMath.abs(delta);
        }
        return sum;
    }

    /**
     * Distance between two vectors.
     * This method computes the distance consistent with
     * L<sub>1</sub> norm, i.e. the sum of the absolute values of
     * elements differences.
     *
     * @param v Vector to which distance is requested.
     * @return the distance between two vectors.
     * @throws DimensionMismatchException if {@code v} is not the same size
     * as this vector.
     * @see #getDistance(RealVector)
     * @see #getL1Distance(ArrayRealVector)
     * @see #getLInfDistance(ArrayRealVector)
     * @see #getNorm()
     */
    public double getL1Distance(ArrayRealVector v) {
        return getL1Distance(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public double getLInfDistance(RealVector v) {
        if (v instanceof ArrayRealVector) {
            return getLInfDistance((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            double max = 0;
            for (int i = 0; i < data.length; ++i) {
                final double delta = data[i] - v.getEntry(i);
                max = FastMath.max(max, FastMath.abs(delta));
            }
            return max;
        }
    }

    /** {@inheritDoc} */
    @Override
    public double getLInfDistance(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        double max = 0;
        for (int i = 0; i < data.length; ++i) {
            final double delta = data[i] - v[i];
            max = FastMath.max(max, FastMath.abs(delta));
        }
        return max;
    }

    /**
     * Distance between two vectors.
     * This method computes the distance consistent with
     * L<sub>&infin;</sub> norm, i.e. the max of the absolute values of
     * elements differences.
     *
     * @param v Vector to which distance is requested.
     * @return the distance between two vectors.
     * @exception IllegalArgumentException if {@code v} is not the same size as this
     * @see #getDistance(RealVector)
     * @see #getL1Distance(ArrayRealVector)
     * @see #getLInfDistance(ArrayRealVector)
     * @see #getNorm()
     */
    public double getLInfDistance(ArrayRealVector v) {
        return getLInfDistance(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public RealVector unitVector() {
        final double norm = getNorm();
        if (norm == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }
        return mapDivide(norm);
    }

    /** {@inheritDoc} */
    @Override
    public void unitize() {
        final double norm = getNorm();
        if (norm == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM);
        }
        mapDivideToSelf(norm);
    }

    /** {@inheritDoc} */
    public RealVector projection(RealVector v) {
        return v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /** {@inheritDoc} */
    @Override
    public RealVector projection(double[] v) {
        return projection(new ArrayRealVector(v, false));
    }

    /**
     * Find the orthogonal projection of this vector onto another vector.
     *
     * @param v Vector onto which instance must be projected.
     * @return Projection of this instance onto {@code v}.
     * @throws DimensionMismatchException if {@code v} is not the same size as
     * this vector.
     */
    public ArrayRealVector projection(ArrayRealVector v) {
        return (ArrayRealVector) v.mapMultiply(dotProduct(v) / v.dotProduct(v));
    }

    /** {@inheritDoc} */
    @Override
    public RealMatrix outerProduct(RealVector v)
        throws IllegalArgumentException {
        if (v instanceof ArrayRealVector) {
            return outerProduct((ArrayRealVector) v);
        } else {
            checkVectorDimensions(v);
            final int m = data.length;
            final RealMatrix out = MatrixUtils.createRealMatrix(m, m);
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data.length; j++) {
                    out.setEntry(i, j, data[i] * v.getEntry(j));
                }
            }
            return out;
        }
    }

    /**
     * Compute the outer product.
     * @param v vector with which outer product should be computed
     * @return the square matrix outer product between instance and v
     * @exception IllegalArgumentException if v is not the same size as this
     */
    public RealMatrix outerProduct(ArrayRealVector v)
        throws IllegalArgumentException {
        return outerProduct(v.data);
    }

    /** {@inheritDoc} */
    @Override
    public RealMatrix outerProduct(double[] v)
        throws IllegalArgumentException {
        checkVectorDimensions(v.length);
        final int m = data.length;
        final RealMatrix out = MatrixUtils.createRealMatrix(m, m);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                out.setEntry(i, j, data[i] * v[j]);
            }
        }
        return out;
    }

    /** {@inheritDoc} */
    public double getEntry(int index) {
        return data[index];
    }

    /** {@inheritDoc} */
    public int getDimension() {
        return data.length;
    }

    /** {@inheritDoc} */
    public RealVector append(RealVector v) {
        try {
            return new ArrayRealVector(this, (ArrayRealVector) v);
        } catch (ClassCastException cce) {
            return new ArrayRealVector(this, v);
        }
    }

    /**
     * Construct a vector by appending a vector to this vector.
     *
     * @param v Vector to append to this one.
     * @return a new vector.
     */
    public ArrayRealVector append(ArrayRealVector v) {
        return new ArrayRealVector(this, v);
    }

    /** {@inheritDoc} */
    public RealVector append(double in) {
        final double[] out = new double[data.length + 1];
        System.arraycopy(data, 0, out, 0, data.length);
        out[data.length] = in;
        return new ArrayRealVector(out, false);
    }

    /** {@inheritDoc} */
    public RealVector append(double[] in) {
        return new ArrayRealVector(this, in);
    }

    /** {@inheritDoc} */
    public RealVector getSubVector(int index, int n) {
        ArrayRealVector out = new ArrayRealVector(n);
        try {
            System.arraycopy(data, index, out.data, 0, n);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + n - 1);
        }
        return out;
    }

    /** {@inheritDoc} */
    public void setEntry(int index, double value) {
        try {
            data[index] = value;
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setSubVector(int index, RealVector v) {
        try {
            try {
                set(index, (ArrayRealVector) v);
            } catch (ClassCastException cce) {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    data[i] = v.getEntry(i-index);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.getDimension() - 1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setSubVector(int index, double[] v) {
        try {
            System.arraycopy(v, 0, data, index, v.length);
        } catch (IndexOutOfBoundsException e) {
            checkIndex(index);
            checkIndex(index + v.length - 1);
        }
    }

    /**
     * Set a set of consecutive elements.
     *
     * @param index Index of first element to be set.
     * @param v Vector containing the values to set.
     * @throws org.apache.commons.math.exception.OutOfRangeException
     * if the index is inconsistent with vector size.
     */
    public void set(int index, ArrayRealVector v) {
        setSubVector(index, v.data);
    }

    /** {@inheritDoc} */
    @Override
    public void set(double value) {
        Arrays.fill(data, value);
    }

    /** {@inheritDoc} */
    @Override
    public double[] toArray(){
        return data.clone();
    }

    /** {@inheritDoc} */
    @Override
    public String toString(){
        return DEFAULT_FORMAT.format(this);
    }

    /**
     * Check if instance and specified vectors have the same dimension.
     *
     * @param v Vector to compare instance with.
     * @throws DimensionMismatchException if the vectors do not
     * have the same dimension.
     */
    @Override
    protected void checkVectorDimensions(RealVector v) {
        checkVectorDimensions(v.getDimension());
    }

    /**
     * Check if instance dimension is equal to some expected value.
     *
     * @param n Expected dimension.
     * @throws DimensionMismatchException if the dimension is
     * inconsistent with vector size.
     */
    @Override
    protected void checkVectorDimensions(int n) {
        if (data.length != n) {
            throw new DimensionMismatchException(data.length, n);
        }
    }

    /**
     * Check if any coordinate of this vector is {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is {@code NaN},
     * {@code false} otherwise.
     */
    public boolean isNaN() {
        for (double v : data) {
            if (Double.isNaN(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether any coordinate of this vector is infinite and none
     * are {@code NaN}.
     *
     * @return {@code true} if any coordinate of this vector is infinite and
     * none are {@code NaN}, {@code false} otherwise.
     */
    public boolean isInfinite() {
        if (isNaN()) {
            return false;
        }

        for (double v : data) {
            if (Double.isInfinite(v)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Test for the equality of two real vectors.
     * If all coordinates of two real vectors are exactly the same, and none are
     * {@code NaN}, the two real vectors are considered to be equal.
     * {@code NaN} coordinates are considered to affect globally the vector
     * and be equals to each other - i.e, if either (or all) coordinates of the
     * real vector are equal to {@code NaN}, the real vector is equal to
     * a vector with all {@code NaN} coordinates.
     *
     * @param other Object to test for equality.
     * @return {@code true} if two vector objects are equal, {@code false} if
     * {@code other} is null, not an instance of {@code RealVector}, or
     * not equal to this {@code RealVector} instance.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || !(other instanceof RealVector)) {
            return false;
        }

        RealVector rhs = (RealVector) other;
        if (data.length != rhs.getDimension()) {
            return false;
        }

        if (rhs.isNaN()) {
            return this.isNaN();
        }

        for (int i = 0; i < data.length; ++i) {
            if (data[i] != rhs.getEntry(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get a hashCode for the real vector.
     * All {@code NaN} values have the same hash code.
     *
     * @return a hash code.
     */
    @Override
    public int hashCode() {
        if (isNaN()) {
            return 9;
        }
        return MathUtils.hash(data);
    }
}
