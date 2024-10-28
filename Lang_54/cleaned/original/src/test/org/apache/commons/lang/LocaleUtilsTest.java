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
package org.apache.commons.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Unit tests for {@link LocaleUtils}.
 *
 * @author Chris Hyzer
 * @author Stephen Colebourne
 * @version $Id$
 */
public class LocaleUtilsTest extends TestCase {

    private static final Locale LOCALE_EN = new Locale("en", "");
    private static final Locale LOCALE_EN_US = new Locale("en", "US");
    private static final Locale LOCALE_EN_US_ZZZZ = new Locale("en", "US", "ZZZZ");
    private static final Locale LOCALE_FR = new Locale("fr", "");
    private static final Locale LOCALE_FR_CA = new Locale("fr", "CA");
    private static final Locale LOCALE_QQ = new Locale("qq", "");
    private static final Locale LOCALE_QQ_ZZ = new Locale("qq", "ZZ");

    /**
     * Constructor.
     * 
     * @param name
     */
    public LocaleUtilsTest(String name) {
        super(name);
    }

    /**
     * Main.
     * @param args
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Run the test cases as a suite.
     * @return the Test
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(LocaleUtilsTest.class);
        suite.setName("LocaleUtilsTest Tests");
        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();

    }

/**
     * Pass in a valid language, test toLocale.
     *
     * @param localeString to pass to toLocale()
     * @param language of the resulting Locale
     * @param country of the resulting Locale
     * @param variant of the resulting Locale
     */
    private void assertValidToLocale(
            String localeString, String language, 
            String country, String variant) {
        Locale locale = LocaleUtils.toLocale(localeString);
        assertNotNull("valid locale", locale);
        assertEquals(language, locale.getLanguage());
        assertEquals(country, locale.getCountry());
        assertEquals(variant, locale.getVariant());
        
    }
    
    /**
     * Tests #LANG-328 - only language+variant
     */
    public void testLang328() {
        assertValidToLocale("fr__POSIX", "fr", "", "POSIX");
    }
}
