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

package org.apache.commons.codec.language.bm;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests PhoneticEngine.
 * 
 * @author Apache Software Foundation
 * @since 2.0
 */
public class PhoneticEngineTest {

    @Test(timeout = 10000L)
    public void testEncode() {
        
	// see CODEC-187
        // comparison: http://stevemorse.org/census/soundex.html

        Map<String, String> args = new TreeMap<String, String>();
        args.put("nameType", "GENERIC");
        args.put("ruleType", "APPROX");

        assertEquals(encode(args, true, "abram"), "Ybram|Ybrom|abram|abran|abrom|abron|avram|avrom|obram|obran|obrom|obron|ovram|ovrom");
        assertEquals(encode(args, true, "Bendzin"), "bndzn|bntsn|bnzn|vndzn|vntsn");

        args.put("nameType", "ASHKENAZI");
        args.put("ruleType", "APPROX");

        assertEquals(encode(args, true, "abram"), "Ybram|Ybrom|abram|abrom|avram|avrom|imbram|imbrom|obram|obrom|ombram|ombrom|ovram|ovrom");
        assertEquals(encode(args, true, "Halpern"), "YlpYrn|Ylpirn|alpYrn|alpirn|olpYrn|olpirn|xalpirn|xolpirn");

    }

    /**
     * This code is similar in style to code found in Solr:
     * solr/core/src/java/org/apache/solr/analysis/BeiderMorseFilterFactory.java
     *
     * Making a JUnit test out of it to protect Solr from possible future
     * regressions in Commons-Codec.
     */
    private static String encode(final Map<String, String> args, final boolean concat, final String input) {
        HashSet<String> languageSet;
        PhoneticEngine engine;

        // PhoneticEngine = NameType + RuleType + concat
        // we use common-codec's defaults: GENERIC + APPROX + true
        final String nameTypeArg = args.get("nameType");
        final NameType nameType = (nameTypeArg == null) ? NameType.GENERIC : NameType.valueOf(nameTypeArg);

        final String ruleTypeArg = args.get("ruleType");
        final RuleType ruleType = (ruleTypeArg == null) ? RuleType.APPROX : RuleType.valueOf(ruleTypeArg);

        engine = new PhoneticEngine(nameType, ruleType, concat);

        // LanguageSet: defaults to automagic, otherwise a comma-separated list.
        final String languageSetArg = args.get("languageSet");
        if (languageSetArg == null || languageSetArg.equals("auto")) {
            languageSet = null;
        } else {
            languageSet = new HashSet<String>(Arrays.asList(languageSetArg.split(",")));
        }

        /*
            org/apache/lucene/analysis/phonetic/BeiderMorseFilter.java (lines 96-98) does this:

            encoded = (languages == null)
                ? engine.encode(termAtt.toString())
                : engine.encode(termAtt.toString(), languages);

            Hence our approach, below:
        */
        if (languageSet == null) {
            return engine.encode(input);
        } else {
            return engine.phoneticUtf8(input, languageSet);
        }
    }
}
