/*
 * Copyright (c) 2012 Nimbits Inc.
 *
 *    http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.transactions.counter;

import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.counter.CounterService;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.assertNotNull;


public class CounterServiceImplTest extends NimbitsServletTest {

    @Autowired
    CounterService counterService;

    @Test
    public void testIncrementCounter() throws Exception {
        counterService.createShards("foo");

        for (int i = 0; i < 10; i++) {
            counterService.incrementCounter("foo");
        }
        Assert.assertEquals(counterService.getCount("foo"), 10);

    }

    @Test
    public void testIncrementDateCounter() throws Exception {
        counterService.updateDateCounter("foo");
        Date t = null;
        for (int i = 0; i < 10; i++) {

            t = counterService.updateDateCounter("foo");

        }
        assertNotNull(t);


    }


    @Test
    public void testGetCount() throws Exception {
        counterService.createShards("foo");

        Assert.assertEquals(counterService.getCount("foo"), 0);
    }
}
