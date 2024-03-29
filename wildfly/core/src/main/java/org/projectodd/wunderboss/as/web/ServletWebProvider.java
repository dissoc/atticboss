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

package top.atticboss.as.web;

import top.atticboss.ComponentProvider;
import top.atticboss.Options;
import top.atticboss.as.ActionConduit;
import top.atticboss.as.web.ServletWeb;
import top.atticboss.web.Web;

import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicLong;

public class ServletWebProvider implements ComponentProvider<Web> {

    public ServletWebProvider(ServletContext servletContext, ActionConduit actionConduit, AtomicLong sharedTimeout) {
        this.servletContext = servletContext;
        this.actionConduit = actionConduit;
        this.sharedTimeout = sharedTimeout;

    }
    @Override
    public Web create(String name, Options ignored) {
        return new ServletWeb(name, this.servletContext, this.actionConduit, this.sharedTimeout);
    }

    private final ServletContext servletContext;
    private final ActionConduit actionConduit;
    private final AtomicLong sharedTimeout;
}
