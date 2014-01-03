/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.framework.ExternalService;
import net.alternativmud.lib.NamingThreadFactory;
import net.alternativmud.lib.debug.BusLogger;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import org.jboss.netty.handler.codec.http.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import org.jboss.netty.handler.codec.http.websocketx.*;
import org.jboss.netty.util.CharsetUtil;

/**
 * Ten serwer tworzy dla każdego połączenia Szynę Wydarzeń (EventBus), która
 * następnie może być przechwycona przez odpowiednią klasę. Tutaj dla odmiany
 * używam gniazd NIO. Możesz sobie porównać trzy opcje tworzenia serwerów w
 * javie.
 *
 * TODO: Trzeba to przepisać na Netty, bo blokuje się przy różnowątkowym
 * odczycie i zapisie.
 *
 * @author jblew
 */
public class WSEBusServer implements ExternalService {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    static {
        JSON_MAPPER.setSerializationConfig(JSON_MAPPER.getSerializationConfig().without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS));
    }

    private final Map<Channel, ChannelBus> buses = Collections.synchronizedMap(new HashMap<Channel, ChannelBus>());
    private final ExecutorService busExecutor = Executors.newCachedThreadPool(new NamingThreadFactory("WSEBusServer-bus-reader"));
    private final EventBus globalEBus;
    private ServerBootstrap tcpBootstrap;
    public WSEBusServer(EventBus globalEBus) {
        this.globalEBus = globalEBus;
    }

    public void start() throws IOException {
        tcpBootstrap = new ServerBootstrap( //Tworzymy serwer
                new NioServerSocketChannelFactory( //Ta klasa mówi, że będzie używał Socketów NIO
                Executors.newCachedThreadPool(), //Dodajemy dwie automatyczne pule wątków
                Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        tcpBootstrap.setPipelineFactory(new ChannelPipelineFactory() {//Ta anonimowa klasa jest odpowiedzialna za tworzenie nowych gniazd
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline(); //Tworzymy nowe gniazdo
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("handler", new Handler());//Handler jest tworzony dla każdego nowego wątku i obsługuje go potem.
                return pipeline;
            }

        });

        tcpBootstrap.setOption("reuseAddress", true);

        tcpBootstrap.bind(new InetSocketAddress("0.0.0.0", getPort()));

        /*
         * SocketChannel userChannel = serverChannel.accept(); ChannelBus c =
         * new ChannelBus(userChannel); c.start(); buses.put(userChannel, c);
         */
    }

    public void stop() throws IOException {
        for (Channel chan : buses.keySet()) {
            ChannelBus bus = buses.get(chan);
            bus.stop();
            bus.close();
        }

        busExecutor.shutdown();
        boolean finished = false;
        try {
            finished = busExecutor.awaitTermination(150, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
        }
        if (!finished) {
            busExecutor.shutdownNow();
        }

        if (tcpBootstrap != null) {
            tcpBootstrap.releaseExternalResources();
            tcpBootstrap = null;
        }
    }

    @Override
    public int getPort() {
        return 8887;
    }

    @Override
    public boolean isBind() {
        return true;
    }

    @Override
    public String getDescription() {
        return "WS EventBus Server";
    }
    
    @Override
    public ExternalService.ProtocolType getProtocolType() {
        return ExternalService.ProtocolType.TCP;
    }

    public Map<Channel, ChannelBus> getBuses() {
        return Collections.unmodifiableMap(buses);
    }

    private class Handler extends SimpleChannelUpstreamHandler {
        public static final String WEBSOCKET_PATH = "/websocket";
        private WebSocketServerHandshaker handshaker;
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            Object msg = e.getMessage();
            if (msg instanceof HttpRequest) {
                handleHttpRequest(ctx, (HttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
        }

        private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
            // Allow only GET methods.
            if (req.getMethod() != GET) {
                sendHttpResponse(ctx, req, new DefaultHttpResponse(HTTP_1_1, FORBIDDEN));
                return;
            }
            // Send the demo page and favicon.ico
            switch (req.getUri()) {
                case "/": {
                    HttpResponse res = new DefaultHttpResponse(HTTP_1_1, FORBIDDEN);
                    sendHttpResponse(ctx, req, res);
                    return;
                }
                case "/favicon.ico": {
                    HttpResponse res = new DefaultHttpResponse(HTTP_1_1, FORBIDDEN);
                    sendHttpResponse(ctx, req, res);
                    return;
                }
            }

            // Handshake
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    this.getWebSocketLocation(req), null, false);
            this.handshaker = wsFactory.newHandshaker(req);
            if (this.handshaker == null) {
                wsFactory.sendUnsupportedWebSocketVersionResponse(ctx.getChannel());
            } else {
                this.handshaker.handshake(ctx.getChannel(), req);

                ChannelBus c = new ChannelBus(ctx.getChannel());
                c.start();
                buses.put(ctx.getChannel(), c);
                c.register(new StandardBusSubscriber(c));
                c.register(new BusLogger());

                //App.getApp().getSystemEventBus().post(new GameBusAcceptedEvent(c));
            }
        }
        /*
         * This is the meat of the server event processing178 Copyright © 2010
         * by Aditya Yadav (http://adityayadav.com) Here we square the number
         * and return it
         */

        private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
            if (frame instanceof CloseWebSocketFrame) {
                this.handshaker.close(ctx.getChannel(), (CloseWebSocketFrame) frame);
                return;
            } else if (frame instanceof PingWebSocketFrame) {
                ctx.getChannel().write(new PongWebSocketFrame(frame.getBinaryData()));
                return;
            } else if (!(frame instanceof TextWebSocketFrame)) {
                throw new UnsupportedOperationException(frame.getClass().getName() + " frame types not supported");
            }

            System.out.println(((TextWebSocketFrame) frame).getText());
            try {
                //System.out.println("SRV.MSG_RECV");
                String in = ((TextWebSocketFrame) frame).getText();
                JsonNode root = JSON_MAPPER.readTree(in);
                String className = root.get("className").getTextValue();
                try {
                    Object o = JSON_MAPPER.readValue(root.get("object"), Class.forName(className));
                    //System.out.println(o.getClass()+" o.toString(): "+o.toString());
                    buses.get(ctx.getChannel()).postOnlyOnLocalBus(o);
                } catch (ClassNotFoundException | IOException ex) {
                    Logger.getLogger(WSEBusServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(WSEBusServer.class.getName()).log(Level.SEVERE, "Exception in messageReceived", ex);
            }
        }

        private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
            // Generate an error page if response status code is not OK (200).
            if (res.getStatus().getCode() != 200) {
                res.setContent(
                        ChannelBuffers.copiedBuffer(
                        res.getStatus().toString(), CharsetUtil.UTF_8));
                setContentLength(res, res.getContent().readableBytes());
            }
            // Send the response and close the connection if necessary.
            ChannelFuture f = ctx.getChannel().write(res);
            if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }

        private String getWebSocketLocation(HttpRequest req) {
            return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            buses.get(e.getChannel()).close();
        }

        /**
         * Ta metoda jest wykonywana podczas błędu.
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            Logger.getLogger(RemoteAdminServer.class.getName()).log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
            e.getChannel().close();
        }

        @Override
        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            super.handleUpstream(ctx, e);
            //System.out.println("Event: " + e.toString() + " (" + e.getClass().getName() + ")");
        }

    }

    public class ChannelBus extends EventBus {
        private final Channel channel;
        private ChannelBus(Channel channel) {
            this.channel = channel;
            globalEBus.post(new EBusConnected(this, (InetSocketAddress) channel.getRemoteAddress()));
        }

        private void start() {
        }

        private void stop() {
            close();
        }

        /**
         * Posts only locally
         *
         * @param o
         */
        public void postOnlyOnLocalBus(Object o) {
            super.post(o);
        }

        @Override
        public void post(Object o) {
            postOnlyOnLocalBus(o);
            if (!(o instanceof DeadEvent)) {
                //System.out.println("Server.Sending(" + o.getClass().getName() + "): " + o.toString());
                try {
                    channel.write(new TextWebSocketFrame("{\"className\": \"" + o.getClass().getName() + "\","
                            + "\"object\": " + JSON_MAPPER.writeValueAsString(o) + "}"));
                } catch (Exception ex) {
                    Logger.getLogger(WSEBusServer.class.getName()).log(Level.SEVERE, "Exception in server.post", ex);
                }
            }
        }

        private void close() {
            channel.close();
            EBusClosed evt = new EBusClosed(this, (InetSocketAddress) channel.getRemoteAddress());
            this.postOnlyOnLocalBus(evt);
            globalEBus.post(evt);
        }

    }

    public static class EBusConnected {
        public final EventBus ebus;
        public final InetSocketAddress addr;
        public EBusConnected(EventBus ebus, InetSocketAddress addr) {
            this.ebus = ebus;
            this.addr = addr;
        }

    }

    public static class EBusClosed {
        public final EventBus ebus;
        public final InetSocketAddress addr;
        public EBusClosed(EventBus ebus, InetSocketAddress addr) {
            this.ebus = ebus;
            this.addr = addr;
        }

    }
}
