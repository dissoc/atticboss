/*
 * Copyright 2014-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.atticboss.as.messaging;

import org.projectodd.atticboss.ComponentProvider;
import org.projectodd.atticboss.Options;
import org.projectodd.atticboss.AtticBoss;
import org.projectodd.atticboss.as.ASUtils;
import org.projectodd.atticboss.as.AtticBossService;
import org.projectodd.atticboss.as.messaging.eap.EAPDestinationManager;
import org.projectodd.atticboss.as.messaging.wildfly.WildFlyDestinationManager;
import org.projectodd.atticboss.messaging.Messaging;

public class ASMessagingProvider implements ComponentProvider<Messaging> {

    @Override
    public Messaging create(String name, Options options) {
        final AtticBossService service = (AtticBossService)AtticBoss.options().get(AtticBossService.KEY);
        ASDestinationManager destManager;

        if (ASUtils.containerIsEAP6()) {
            destManager = new EAPDestinationManager(service.serviceTarget(),
                                                    ASUtils.messagingServiceName(),
                                                    service.namingContext());
        } else {
            destManager = new WildFlyDestinationManager(service.serviceTarget(),
                                                        ASUtils.messagingServiceName());
        }

        return new ASMessaging(name, service, destManager, options);
    }
}
