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

package org.projectodd.atticboss.as;

import org.jboss.modules.Module;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceRegistryException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CoreServiceActivator implements ServiceActivator {
    @Override
    public void activate(ServiceActivatorContext serviceActivatorContext) throws ServiceRegistryException {
        Module module = Module.forClass(CoreServiceActivator.class);
        String deploymentName = module.getIdentifier().getName();
        if (deploymentName.startsWith("deployment.")) {
            deploymentName = deploymentName.replace("deployment.", "");
        }

        Context namingContext;
        try {
            namingContext = new InitialContext();
        } catch (NamingException e) {
            throw new ServiceRegistryException(e);
        }

        AtticBossService service = new AtticBossService(deploymentName,
                                                    serviceActivatorContext.getServiceRegistry(),
                                                    serviceActivatorContext.getServiceTarget(),
                                                    namingContext);
        serviceActivatorContext.getServiceTarget()
                .addService(AtticBossService.serviceName(deploymentName), service)
                .setInitialMode(ServiceController.Mode.ACTIVE)
                .install();
    }
}
