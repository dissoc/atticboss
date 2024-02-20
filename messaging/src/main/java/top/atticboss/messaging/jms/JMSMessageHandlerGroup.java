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

package top.atticboss.messaging.jms;

import top.atticboss.Options;
import top.atticboss.codecs.Codecs;
import top.atticboss.messaging.Context;
import top.atticboss.messaging.Destination;
import top.atticboss.messaging.Listener;
import top.atticboss.messaging.MessageHandler;
import top.atticboss.messaging.MessageHandlerGroup;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

public class JMSMessageHandlerGroup extends MessageHandlerGroup {

    public JMSMessageHandlerGroup(JMSSpecificContext context,
                                  MessageHandler handler,
                                  Codecs codecs,
                                  JMSDestination destination,
                                  Options<Destination.ListenOption> options) {
        super(context, handler, codecs, destination, options);
    }

    @Override
    public Listener createListener(final MessageHandler handler,
                                   final Codecs codecs,
                                   final Destination destination,
                                   final Context context,
                                   final Options<Destination.ListenOption> options) throws Exception {
        JMSSpecificContext subContext =
                ((JMSSpecificContext)context)
                        .createChildContext((Context.Mode) options.get(Destination.ListenOption.MODE));
        return new JMSListener(handler,
                               codecs,
                               destination,
                               subContext,
                               createConsumer(destination, subContext, options)).start();
    }

    protected MessageConsumer createConsumer(final Destination destination,
                                             final Context context,
                                             final Options<Destination.ListenOption> options) throws JMSException {
        return ((JMSSpecificContext)context)
                .jmsSession()
                .createConsumer(((JMSDestination) destination).jmsDestination(),
                                options.getString(Destination.ListenOption.SELECTOR));
    }
}
