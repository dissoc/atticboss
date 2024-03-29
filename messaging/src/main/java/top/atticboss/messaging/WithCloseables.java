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

package top.atticboss.messaging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WithCloseables implements HasCloseables {
    @Override
    public void addCloseable(Object closeable) {
        this.closeables.add(closeable);
    }

    @Override
    public void closeCloseables() throws Exception {
        for(Object each: this.closeables) {
            if (each instanceof AutoCloseable) {
                ((AutoCloseable) each).close();
            } else {
                try {
                    Method close = each.getClass().getMethod("close");
                    close.invoke(each);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {

                    throw new RuntimeException("Can't close object of class " + each.getClass().getName(), e);
                }
            }
        }

        this.closeables.clear();
    }

    private final List<Object> closeables = new ArrayList<>();
}
