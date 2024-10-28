/*
 * This file was automatically generated by EvoSuite
 * Thu Aug 31 12:21:46 GMT 2023
 */

package org.apache.commons.math3.fraction;

import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.math3.fraction.Fraction;
import org.junit.runner.RunWith;
import shaded.org.evosuite.runtime.EvoRunner;
import shaded.org.evosuite.runtime.EvoRunnerParameters;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true, useVFS = true, useVNET = true, resetStaticState = true, separateClassLoader = true) 
public class Fraction_ESTest extends Fraction_ESTest_scaffolding {

  @Test(timeout = 4000)
  public void test0()  throws Throwable  {
      Fraction fraction0 = new Fraction((-661), 4);
      assertEquals((-661), fraction0.getNumerator());
      assertEquals((-16525.0), fraction0.percentageValue(), 0.01);
  }

  @Test(timeout = 4000)
  public void test1()  throws Throwable  {
      Fraction fraction0 = new Fraction(4);
      assertEquals(1, fraction0.getDenominator());
      assertEquals(4.0F, fraction0.floatValue(), 0.01F);
  }

  @Test(timeout = 4000)
  public void test2()  throws Throwable  {
      Fraction fraction0 = new Fraction(0.9689123630523682, 869);
      assertEquals(187, fraction0.getNumerator());
  }

  @Test(timeout = 4000)
  public void test3()  throws Throwable  {
      Fraction fraction0 = new Fraction((-41.72799), 0.0, 61);
      assertEquals(100000, fraction0.getDenominator());
  }

  @Test(timeout = 4000)
  public void test4()  throws Throwable  {
      Fraction fraction0 = new Fraction(0.0);
      assertEquals(0.0F, fraction0.floatValue(), 0.01F);
  }
}