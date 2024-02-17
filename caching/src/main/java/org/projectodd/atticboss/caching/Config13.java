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

package org.projectodd.atticboss.caching;

import org.projectodd.atticboss.Options;

public class Config13 extends Config {

    public Config13(Options<Caching.CreateOption> options) {
        super(options);
    }

    // the keys are now stored as byte arrays and equivalence is now checked
    // by comparing the byte arrays
    // @deprecated Equivalence is to be removed (byte[] are directly supported)

    // void equivalate() {
    //     builder.dataContainer().
    //     builder.dataContainer().keyEquivalence(EQUIVALENCE);
    // }

    void persist() {
        Object v = options.get(Caching.CreateOption.PERSIST);
        if (v instanceof Boolean && (boolean) v) {
            builder.persistence().addSingleFileStore();
        }
        if (v instanceof String) {
            builder.persistence().addSingleFileStore().location(v.toString());
        }
    }

//    static org.infinispan.commons.equivalence.Equivalence EQUIVALENCE = new Equivalence13();
}
