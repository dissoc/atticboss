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

package top.atticboss.web.undertow.async.websocket;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedBinaryMessage;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.CloseMessage;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;
import top.atticboss.web.undertow.AttachableHttpHandler;
import org.xnio.Buffers;
import org.xnio.ChannelListener;
import org.xnio.Pooled;

import java.io.IOException;
import java.nio.ByteBuffer;


public class UndertowWebsocket {
    public static final AttachmentKey<WebSocketProtocolHandshakeHandler> HANDSHAKE_ATTACHMENT_KEY =
            AttachmentKey.create(WebSocketProtocolHandshakeHandler.class);

    private static final AttachmentKey<DelegatingUndertowEndpoint> ENDPOINT_ATTACHMENT_KEY =
            AttachmentKey.create(DelegatingUndertowEndpoint.class);

    public static HttpHandler createHandler(final WebsocketInitHandler checker,
                                            final HttpHandler next) {
        WebSocketConnectionCallback callback = new WebSocketConnectionCallback() {
            public void onConnect (WebSocketHttpExchange exchange, WebSocketChannel channel) {
                final DelegatingUndertowEndpoint endpoint = exchange.getAttachment(ENDPOINT_ATTACHMENT_KEY);
                if (endpoint != null) {
                    endpoint.onOpen(channel, exchange);
                    channel.addCloseTask(new ChannelListener<WebSocketChannel>() {
                        @Override
                        public void handleEvent(WebSocketChannel channel) {
                            endpoint.onClose(channel, new CloseMessage(CloseMessage.GOING_AWAY, null));
                        }
                    });
                    channel.getReceiveSetter().set(new AbstractReceiveListener() {
                        protected void onError (WebSocketChannel channel, Throwable error) {
                            endpoint.onError(channel, error);
                        }
                        protected void onCloseMessage (CloseMessage message, WebSocketChannel channel) {
                            endpoint.onClose(channel, message);
                        }
                        protected void onFullTextMessage (WebSocketChannel channel, BufferedTextMessage message) {
                            endpoint.onMessage(channel, message.getData());
                        }
                        protected void onFullBinaryMessage (WebSocketChannel channel, BufferedBinaryMessage message) {
                            Pooled<ByteBuffer[]> pooled = message.getData();
                            try {
                                ByteBuffer[] payload = pooled.getResource();
                                endpoint.onMessage(channel, toArray(payload));
                            } finally {
                                pooled.free();
                            }
                        }
                    });
                    channel.resumeReceives();
                } else {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Error closing websocket", e);
                    }
                }
            }
        };

        final HttpHandler downstream = next==null ? ResponseCodeHandler.HANDLE_404 : next;
        final WebSocketProtocolHandshakeHandler wsHandler =
                new WebSocketProtocolHandshakeHandler(callback, downstream);
        final AttachableHttpHandler handler = new AttachableHttpHandler() {
            @Override
            public void handleRequest(HttpServerExchange exchange) throws Exception {
                HeaderValues upgrade = exchange.getRequestHeaders().get(Headers.UPGRADE);
                if (upgrade != null && "websocket".equalsIgnoreCase(upgrade.peek())) {
                    final DelegatingUndertowEndpoint endpoint = new DelegatingUndertowEndpoint();
                    if (checker.shouldConnect(exchange, endpoint)) {
                        exchange.putAttachment(ENDPOINT_ATTACHMENT_KEY, endpoint);
                        wsHandler.handleRequest(exchange);
                    } else {
                        downstream.handleRequest(exchange);
                    }
                } else {
                    downstream.handleRequest(exchange);
                }
            }
        };

        handler.putAttachment(HANDSHAKE_ATTACHMENT_KEY, wsHandler);

        return handler;
    }

    // Lifted from Undertow's FrameHandler.java
    protected static byte[] toArray(ByteBuffer... payload) {
        if (payload.length == 1) {
            ByteBuffer buf = payload[0];
            if (buf.hasArray() && buf.arrayOffset() == 0 && buf.position() == 0) {
                return buf.array();
            }
        }
        int size = (int) Buffers.remaining(payload);
        byte[] data = new byte[size];
        for (ByteBuffer buf : payload) {
            buf.get(data);
        }
        return data;
    }
}
