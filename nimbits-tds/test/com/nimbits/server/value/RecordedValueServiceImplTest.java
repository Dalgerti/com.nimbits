/*
 * Copyright (c) 2010 Tonic Solutions LLC.
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

package com.nimbits.server.value;

import com.nimbits.client.enums.FilterType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.NimbitsServletTest;
import com.nimbits.server.entity.EntityServiceFactory;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/12
 * Time: 8:51 AM
 */
public class RecordedValueServiceImplTest extends NimbitsServletTest {


    private static final double D = 1.23;
    private static final double D1 = 2.23;
    private static final int D2 = 11;
    private static final double VALUE = 0.1;

    @Test
    public void ignoreByCompressionTest() throws NimbitsException, InterruptedException {
        point.setFilterValue(VALUE);
        EntityServiceFactory.getInstance().addUpdateEntity(point);


        Value value = ValueModelFactory.createValueModel(D);
        Value value2 = ValueModelFactory.createValueModel(D);
        Value value3 = ValueModelFactory.createValueModel(D1);

        RecordedValueServiceFactory.getInstance().recordValue(user, point, value);

        RecordedValueServiceImpl impl = new RecordedValueServiceImpl();
        assertTrue(impl.ignoreByFilter(point, value2));
        assertFalse(impl.ignoreByFilter(point, value3));

        point.setFilterValue(10);
        point.setFilterType(FilterType.ceiling);

        assertFalse(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(D1)));
        assertTrue(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(D2)));

        point.setFilterType(FilterType.floor);
        assertTrue(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(D1)));
        assertFalse(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(D2)));

        point.setFilterType(FilterType.none);
        assertFalse(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(D2)));

        point.setFilterType(FilterType.percentageHysteresis);
        EntityServiceFactory.getInstance().addUpdateEntity(point);
      //  pointService.updatePoint(point);
        RecordedValueServiceFactory.getInstance().recordValue(user, point, ValueModelFactory.createValueModel(100));
        Thread.sleep(10);
        assertTrue(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(105)));
        assertTrue(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(95)));
        assertFalse(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(111)));
        assertFalse(impl.ignoreByFilter(point, ValueModelFactory.createValueModel(80)));

    }


}
