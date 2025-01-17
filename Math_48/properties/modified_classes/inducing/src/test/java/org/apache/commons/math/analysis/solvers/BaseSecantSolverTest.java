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
package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.QuinticFunction;
import org.apache.commons.math.analysis.SinFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.XMinus5Function;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.NoBracketingException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Base class for root-finding algorithms tests derived from
 * {@link BaseSecantSolver}.
 *
 * @version $Id$
 */
public abstract class BaseSecantSolverTest {
    /** Returns the solver to use to perform the tests.
     * @return the solver to use to perform the tests
     */
    protected abstract UnivariateRealSolver getSolver();

    /** Returns the expected number of evaluations for the
     * {@link #testQuinticZero} unit test. A value of {@code -1} indicates that
     * the test should be skipped for that solver.
     * @return the expected number of evaluations for the
     * {@link #testQuinticZero} unit test
     */
    protected abstract int[] getQuinticEvalCounts();

    @Test
    public void testSinZero() {
        // The sinus function is behaved well around the root at pi. The second
        // order derivative is zero, which means linear approximating methods
        // still converge quadratically.
        UnivariateRealFunction f = new SinFunction();
        double result;
        UnivariateRealSolver solver = getSolver();

        result = solver.solve(100, f, 3, 4);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 6);
        result = solver.solve(100, f, 1, 4);
        //System.out.println(
        //    "Root: " + result + " Evaluations: " + solver.getEvaluations());
        Assert.assertEquals(result, FastMath.PI, solver.getAbsoluteAccuracy());
        Assert.assertTrue(solver.getEvaluations() <= 7);
    }

    @Test
    public void testQuinticZero() {
        // The quintic function has zeros at 0, +-0.5 and +-1.
        // Around the root of 0 the function is well behaved, with a second
        // derivative of zero a 0.
        // The other roots are less well to find, in particular the root at 1,
        // because the function grows fast for x>1.
        // The function has extrema (first derivative is zero) at 0.27195613
        // and 0.82221643, intervals containing these values are harder for
        // the solvers.
        UnivariateRealFunction f = new QuinticFunction();
        double result;
        UnivariateRealSolver solver = getSolver();
        double atol = solver.getAbsoluteAccuracy();
        int[] counts = getQuinticEvalCounts();

        // Tests data: initial bounds, and expected solution, per test case.
        double[][] testsData = {{-0.2,  0.2,  0.0},
                                {-0.1,  0.3,  0.0},
                                {-0.3,  0.45, 0.0},
                                { 0.3,  0.7,  0.5},
                                { 0.2,  0.6,  0.5},
                                { 0.05, 0.95, 0.5},
                                { 0.85, 1.25, 1.0},
                                { 0.8,  1.2,  1.0},
                                { 0.85, 1.75, 1.0},
                                { 0.55, 1.45, 1.0},
                                { 0.85, 5.0,  1.0},
                               };
        int maxIter = 500;

        for(int i = 0; i < testsData.length; i++) {
            // Skip test, if needed.
            if (counts[i] == -1) continue;

            // Compute solution.
            double[] testData = testsData[i];
            result = solver.solve(maxIter, f, testData[0], testData[1]);
            //System.out.println(
            //    "Root: " + result + " Evaluations: " + solver.getEvaluations());

            // Check solution.
            Assert.assertEquals(result, testData[2], atol);
            Assert.assertTrue(solver.getEvaluations() <= counts[i] + 1);
        }
    }

    @Test
    public void testRootEndpoints() {
        UnivariateRealFunction f = new XMinus5Function();
        UnivariateRealSolver solver = getSolver();

        // End-point is root. This should be a special case in the solver, and
        // the initial end-point should be returned exactly.
        double result = solver.solve(100, f, 5.0, 6.0);
        Assert.assertEquals(5.0, result, 0.0);

        result = solver.solve(100, f, 4.0, 5.0);
        Assert.assertEquals(5.0, result, 0.0);

        result = solver.solve(100, f, 5.0, 6.0, 5.5);
        Assert.assertEquals(5.0, result, 0.0);

        result = solver.solve(100, f, 4.0, 5.0, 4.5);
        Assert.assertEquals(5.0, result, 0.0);
    }

    @Test
    public void testBadEndpoints() {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = getSolver();
        try {  // bad interval
            solver.solve(100, f, 1, -1);
            Assert.fail("Expecting NumberIsTooLargeException - bad interval");
        } catch (NumberIsTooLargeException ex) {
            // expected
        }
        try {  // no bracket
            solver.solve(100, f, 1, 1.5);
            Assert.fail("Expecting NoBracketingException - non-bracketing");
        } catch (NoBracketingException ex) {
            // expected
        }
        try {  // no bracket
            solver.solve(100, f, 1, 1.5, 1.2);
            Assert.fail("Expecting NoBracketingException - non-bracketing");
        } catch (NoBracketingException ex) {
            // expected
        }
    }

    @Test
    public void testSolutionLeftSide() {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = getSolver();
        if (!(solver instanceof BracketedSolution)) return;
        ((BracketedSolution)solver).setAllowedSolutions(
                                                AllowedSolutions.LEFT_SIDE);
        double left = -1.5;
        double right = 0.05;
        for(int i = 0; i < 10; i++) {
            // Test whether the allowed solutions are taken into account.
            double solution = solver.solve(100, f, left, right);
            Assert.assertTrue(solution <= 0.0);

            // Prepare for next test.
            left -= 0.1;
            right += 0.3;
        }
    }

    @Test
    public void testSolutionRightSide() {
        UnivariateRealFunction f = new SinFunction();
        UnivariateRealSolver solver = getSolver();
        if (!(solver instanceof BracketedSolution)) return;
        ((BracketedSolution)solver).setAllowedSolutions(
                                                AllowedSolutions.RIGHT_SIDE);
        double left = -1.5;
        double right = 0.05;
        for(int i = 0; i < 10; i++) {
            // Test whether the allowed solutions are taken into account.
            double solution = solver.solve(100, f, left, right);
            Assert.assertTrue(solution >= 0.0);

            // Prepare for next test.
            left -= 0.1;
            right += 0.3;
        }
    }
}
