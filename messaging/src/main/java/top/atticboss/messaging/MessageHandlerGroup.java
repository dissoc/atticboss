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

import org.jboss.logging.Logger;
import top.atticboss.Options;
import top.atticboss.codecs.Codecs;
import top.atticboss.messaging.Destination.ListenOption;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageHandlerGroup implements Listener {

    public MessageHandlerGroup(Context context,
                               MessageHandler handler,
                               Codecs codecs,
                               Destination destination,
                               Options<ListenOption> options) {
        this.context = context;
        this.handler = handler;
        this.codecs = codecs;
        this.destination = destination;
        this.options = options;
    }

    public abstract Listener createListener(MessageHandler handler, Codecs codecs,
                                            Destination destination, Context context,
                                            Options<ListenOption> options) throws Exception;

    public synchronized MessageHandlerGroup start() throws Exception {
        if (!this.started) {
            Integer option = this.options.getInt(ListenOption.CONCURRENCY);
            int concurrency = option != null ? option : this.destination.defaultConcurrency();
            log.info("Starting listener for '" + this.destination.name() + "' concurrency=" + concurrency);
            while(concurrency-- > 0) {
                listeners.add(createListener(this.handler, this.codecs, this.destination,
                                             this.context, this.options));
            }

            this.started = true;
        }

        return this;
    }

    @Override
    public synchronized void close() throws Exception {
        if (this.started) {
            this.started = false;
            this.context.close();
            for(Listener each : this.listeners) {
                each.close();
            }
            this.listeners.clear();
        }
    }



    private final MessageHandler handler;
    private final Codecs codecs;
    private final Destination destination;
    private final Options<ListenOption> options;
    private final Context context;
    private final List<Listener> listeners = new ArrayList<>();
    private boolean started = false;

    private static final Logger log = Logger.getLogger("top.atticboss.messaging");
}
