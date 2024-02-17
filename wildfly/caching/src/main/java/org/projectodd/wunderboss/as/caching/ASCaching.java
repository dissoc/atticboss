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

package org.projectodd.atticboss.as.caching;

import org.infinispan.Cache;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistry;
import org.projectodd.atticboss.Options;
import org.projectodd.atticboss.AtticBoss;
import org.projectodd.atticboss.as.ASUtils;
import org.projectodd.atticboss.caching.InfinispanCaching;
import org.projectodd.atticboss.caching.Encoder6;
import org.projectodd.atticboss.caching.Encoder8;
import org.projectodd.atticboss.caching.Config;
import org.projectodd.atticboss.caching.KeyEquivalenceCache;
import org.projectodd.atticboss.codecs.Codec;

import java.util.Map;


public class ASCaching extends InfinispanCaching {
    public static final ServiceName WEB_CACHE_MANAGER = ServiceName.JBOSS.append("infinispan", "web");

    public ASCaching(String name, Options<CreateOption> options) {
        super(name, options);
        if (ASUtils.containerIsWildFly10()) {
            this.encoder = new Encoder8();
        } else if (!ASUtils.containerIsWildFly9()) {
            this.encoder = new Encoder6();
        }
        if (ASUtils.containerIsEAP6()) {
            Config.className = "org.projectodd.atticboss.caching.Config5";
        }
    }

    @Override
    public Cache withCodec(Cache cache, Codec codec) {
        Cache c = ASUtils.containerIsEAP6() ? new KeyEquivalenceCache(cache) : cache;
        return super.withCodec(c, codec);
    }

    public synchronized EmbeddedCacheManager manager() {
        if (this.manager == null) {
            this.manager = getWebCacheManager();
        }
        return this.manager;
    }

    protected Options<CreateOption> validate(Map<CreateOption,Object> options) {
        Options<CreateOption> result = new Options<CreateOption>(options);
        String mode = result.getString(CreateOption.MODE);
        // Default mode when in a cluster
        if (mode == null && ASUtils.inCluster()) {
            result.put(CreateOption.MODE, "DIST_SYNC");
        }
        return result;
    }

    private EmbeddedCacheManager getWebCacheManager() {
        ServiceRegistry serviceRegistry = (ServiceRegistry) AtticBoss.options().get("service-registry");
        return (EmbeddedCacheManager) serviceRegistry.getRequiredService(WEB_CACHE_MANAGER).getValue();
    }

    private GlobalConfiguration getGlobalConfiguration() {
        GlobalConfigurationBuilder builder = new GlobalConfigurationBuilder();
        return builder.read(getWebCacheManager().getCacheManagerConfiguration())
            .classLoader(Thread.currentThread().getContextClassLoader())
            .transport().clusterName("wboss")
            .build();
    }
}
