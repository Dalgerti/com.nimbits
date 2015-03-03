/*
 * Copyright (c) 2010 Nimbits Inc.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.calculation;

import com.nimbits.server.math.MathEvaluator;
import com.nimbits.server.math.MathEvaluatorImpl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MathEvaluatorTest {

    @Test
    public void testMath() throws Exception {

        MathEvaluator e = new MathEvaluatorImpl("1+1");
        double r = e.getValue();
        assertEquals(2, r, .0001);


    }

    @Test
    public void testVar1() throws Exception {


        MathEvaluator e = new MathEvaluatorImpl("(x-32) * (5/9)");
        e.addVariable("x", 100);
        double r = e.getValue();
        assertEquals(37.7777778, r, .0001);

    }

    @Test
    public void testVar3() throws Exception {


        MathEvaluator e = new MathEvaluatorImpl("(x-y) * (5/z)");
        e.addVariable("x", 100);
        e.addVariable("y", 32);
        e.addVariable("z", 9);
        double r = e.getValue();
        assertEquals(37.7777778, r, .0001);

    }

    @Test
    public void testRemove() throws Exception {


        MathEvaluator e = new MathEvaluatorImpl("(x-   y)     * (  5/    z)");
        e.addVariable("x", 100);
        e.addVariable("y", 32);
        e.addVariable("z", 9);
        double r = e.getValue();
        assertEquals(37.7777778, r, .0001);

    }
}
