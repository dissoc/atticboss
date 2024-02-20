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

package top.atticboss.ec;

import top.atticboss.Component;
import top.atticboss.Option;

public interface ExecutionContext extends Component, Runnable {
    class CreateOption extends Option {
        /**
         * The context will be a singleton in a clustered envirionment.
         */
        public static final CreateOption SINGLETON = opt("singleton", true, CreateOption.class);
    }

    void setAction(Runnable action);
}
