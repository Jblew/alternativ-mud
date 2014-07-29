/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.telnetserver;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.framework.ExternalService;
import net.alternativmud.lib.event.NetworkEvent;
import net.alternativmud.lib.util.StringWriter;
import net.alternativmud.logic.event.BusClosedEvent;
import net.alternativmud.logic.event.GameBusAcceptedEvent;
import net.alternativmud.logic.event.ReceivedTextFromUser;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

/**
 * Serwer telnetu.
 *
 * @author jblew
 */
public class TelnetServer implements ExternalService {

    private final Map<Channel, TelnetConnectionBus> buses =
            Collections.synchronizedMap(new HashMap<Channel, TelnetConnectionBus>());
    private ServerBootstrap bootstrap;

    public TelnetServer() {
    }

    public void start() throws IOException {
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();

                // Add the text line codec combination first,
                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192,
                        Delimiters.lineDelimiter()));
                pipeline.addLast("decoder", new StringDecoder());
                pipeline.addLast("encoder", new StringEncoder());

                // and then business logic.
                pipeline.addLast("handler", new Handler());

                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(getPort()));

        App.getApp().getServiceManager().register(TelnetServer.class, this);
    }

    public void stop() throws IOException {
        App.getApp().getServiceManager().unregister(TelnetServer.class);

        //To awoid concurrent modification exception I'm copying
        //all buses into tmp list
        LinkedList<TelnetConnectionBus> buses_ = new LinkedList<>();
        for(Channel c : buses.keySet()) {
            TelnetConnectionBus b = buses.get(c);
            buses_.add(b);
        }
        for(TelnetConnectionBus b : buses_) {
            b.println("Sorry, the telnet server is shutting down now."
                    + " It will be online soon. Bye for now.");
            b.close();
        }
        bootstrap.releaseExternalResources();
    }

    @Override
    public int getPort() {
        return 4000;
    }

    @Override
    public boolean isBind() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Telnet server";
    }
    
    @Override
    public ExternalService.ProtocolType getProtocolType() {
        return ExternalService.ProtocolType.TCP;
    }

    private class Handler extends SimpleChannelUpstreamHandler {
        /*
         * @Override public void handleUpstream( ChannelHandlerContext ctx,
         * ChannelEvent e) throws Exception { if (e instanceof
         * ChannelStateEvent) { logger.info(e.toString()); }
         * super.handleUpstream(ctx, e); }
         */

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
                throws Exception {
            // Send greeting for a new connection.
            e.getChannel().write("Welcome to AlternativMUD telnet-server. Your"
                    + " connection has been sent to system bus. Please wait until "
                    + "some subscriber will take care of you.\r\n");

            final TelnetConnectionBus bus = new TelnetConnectionBus(e.getChannel());
            buses.put(e.getChannel(), bus);
            
            App.getApp().getSystemEventBus().post(
                    new ConnectionAcceptedEvent(e.getChannel(), bus, bus));
            App.getApp().getSystemEventBus().post(
                    new GameBusAcceptedEvent(bus));
        }
        
        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
            TelnetConnectionBus bus = buses.get(e.getChannel());
            buses.remove(e.getChannel());
            bus.post(new BusClosedEvent());
        }       

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
            buses.get(e.getChannel()).post(new ReceivedTextFromUser((String)e.getMessage()));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            Logger.getLogger(Handler.class.getName()).log(Level.WARNING,
                    "Unexpected exception from downstream.", e.getCause());
            e.getChannel().close();
        }
    }

    public static class ConnectionAcceptedEvent implements NetworkEvent.ConnectionAcceptedEvent {

        private final Channel chan;
        private final StringWriter writer;
        private final Closeable closeable;

        public ConnectionAcceptedEvent(Channel chan, StringWriter writer, Closeable closeable) {
            this.chan = chan;
            this.writer = writer;
            this.closeable = closeable;
        }

        @Override
        public InetAddress getAddress() {
            return ((InetSocketAddress) chan.getRemoteAddress()).getAddress();
        }

        @Override
        public StringWriter getWriter() {
            return writer;
        }

        @Override
        public Closeable getCloseable() {
            return closeable;
        }
    }
}
