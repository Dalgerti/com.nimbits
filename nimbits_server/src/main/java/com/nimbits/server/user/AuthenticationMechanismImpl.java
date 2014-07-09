/*
 * Copyright (c) 2013 Nimbits Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either expressed or implied.  See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.user;


import com.nimbits.client.enums.ServerSetting;
import com.nimbits.client.model.common.impl.CommonFactory;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.server.ApplicationListener;
import com.nimbits.server.transaction.settings.SettingServiceFactory;
import com.nimbits.server.transaction.settings.SettingsService;
import com.nimbits.server.transaction.user.service.AuthenticationMechanism;

import java.util.Arrays;
import java.util.List;


public class AuthenticationMechanismImpl implements AuthenticationMechanism {

    @Override
    public List<EmailAddress> getCurrentUserEmail() {
        SettingsService settings = SettingServiceFactory.getServiceInstance(ApplicationListener.createEngine());
        return Arrays.asList(CommonFactory.createEmailAddress(settings.getSetting(ServerSetting.admin)));
    }
}
