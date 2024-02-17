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

package org.projectodd.atticboss.messaging.jms;

import org.projectodd.atticboss.AtticBoss;

import javax.transaction.TransactionManager;
import java.lang.reflect.Method;

public class TransactionUtil {
    public static boolean isTransactionActive() {
        try {
            return tm != null && tm.getTransaction() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static final TransactionManager tm;

    static {
        TransactionManager found = null;
        try {
            Class clazz = Class.forName("org.projectodd.atticboss.transactions.Transaction");
            Method method = clazz.getDeclaredMethod("manager");
            Object component = AtticBoss.findOrCreateComponent(clazz);
            found = (TransactionManager) method.invoke(component);
        } catch (Throwable ignored) {}
        tm = found;
    }
}
