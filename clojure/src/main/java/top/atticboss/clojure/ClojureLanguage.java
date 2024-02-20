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

package top.atticboss.clojure;

import clojure.java.api.Clojure;
import clojure.lang.Compiler;
import top.atticboss.Language;
import top.atticboss.LoaderWrapper;
import top.atticboss.AtticBoss;

import java.util.concurrent.Callable;

public class ClojureLanguage implements Language {

    @Override
    public void initialize() {
        this.runtime = new LoaderWrapper(AtticBoss.classLoader());
        // we have to touch Clojure.class so it will init clojure enough for
        // binding the loader's root to work. Without this, we'll NPE
        Clojure.var("clojure.core", "require");
        // we have to bind the loader for cases where the TCCL is the wrong
        // module inside WildFly (this happens for web requests). If LOADER isn't
        // bound, clojure.lang.RT will fall back to the TCCL.
        Compiler.LOADER.bindRoot(AtticBoss.classLoader());
    }

    @Override
    public LoaderWrapper runtime() {
        return this.runtime;
    }

    @Override
    public void shutdown() {
        this.runtime.callInLoader(new Callable() {
            @Override
            public Object call() throws Exception {
                Clojure.var("clojure.core", "require")
                        .invoke(Clojure.var("clojure.core", "symbol")
                                        .invoke("atticboss.util"));
                Clojure.var("clojure.core", "shutdown-agents").invoke();

                return null;
            }
        });
    }

    @Override
    public Object eval(final String strToEval) {
        try {
            return this.runtime.callInLoader(new Callable() {
                @Override
                public Object call() throws Exception {
                    return Clojure.var("clojure.core", "eval")
                            .invoke(Clojure.var("clojure.core", "read-string")
                                            .invoke("(do " + strToEval + ")"));
                }
            });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T coerceToClass(Object object, Class<T> toClass) {
        return (T) object;
    }

    private LoaderWrapper runtime;
}
