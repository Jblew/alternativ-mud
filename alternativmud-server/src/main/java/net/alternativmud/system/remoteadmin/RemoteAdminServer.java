/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.remoteadmin;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.framework.ExternalService;
import net.alternativmud.system.telnetserver.TelnetServer;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.util.CharsetUtil;

/**
 * W Serwerze zdalnej administracji używam biblioteki Netty, abyś mógł zobaczyć
 * różnicę między Socketami a Tym. W Telnecie zostawiłem Sockety.
 *
 * @author jblew
 */
public class RemoteAdminServer implements ExternalService {

    private ServerBootstrap tcpBootstrap;

    public RemoteAdminServer() {
    }

    public synchronized void start() {
        tcpBootstrap = new ServerBootstrap( //Tworzymy serwer
                new NioServerSocketChannelFactory( //Ta klasa mówi, że będzie używał Socketów NIO
                Executors.newCachedThreadPool(), //Dodajemy dwie automatyczne pule wątków
                Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        tcpBootstrap.setPipelineFactory(new ChannelPipelineFactory() {//Ta anonimowa klasa jest odpowiedzialna za tworzenie nowych gniazd

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline(); //Tworzymy nowe gniazdo
                pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));//Ta klasa dzieli wejście na linie (zabezpieczenie przed pokawałkowanym inputem)
                pipeline.addLast("decoder", new StringDecoder()); //Ta klasa zamienia bajty na znaki w utf-ie
                pipeline.addLast("encoder", new StringEncoder()); //Ta też, tylko w drugą stronę

                pipeline.addLast("handler", new RemoteAdminHandler());//Handler jest tworzony dla każdego nowego wątku i obsługuje go potem.
                return pipeline;
            }
        });

        tcpBootstrap.setOption("reuseAddress", true);

        // Bind and start to accept incoming connections.
        tcpBootstrap.bind(new InetSocketAddress("0.0.0.0", 9999));

        App.getApp().getServiceManager().register(RemoteAdminServer.class, this);
    }

    public synchronized void stop() {
        App.getApp().getServiceManager().unregister(RemoteAdminServer.class);
        if (tcpBootstrap != null) {
            tcpBootstrap.releaseExternalResources();
        }
    }

    @Override
    public int getPort() {
        return 9999;
    }

    @Override
    public boolean isBind() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Remote admin server";
    }

    /**
     * Ta klasa jest prywatna, bo nie używamy jej na zewnątrz.
     */
    private static class RemoteAdminHandler extends SimpleChannelUpstreamHandler {

        private AtomicBoolean loggedIn = new AtomicBoolean(false);
        private String loginText = "remoteadmin:rmadminpass";

        /**
         * Ta funkcja jest wykonywana, gdy user połączy się z RemoteAdminem
         */
        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            e.getChannel().write("Welcome to AlternativMUD-server Remote admin console.\r\n");
            e.getChannel().write(Resources.toString(Resources.getResource(App.class, "intro"), Charsets.UTF_8) + "\r\n");
            e.getChannel().write("Please log in.\r\n");
        }

        /**
         * Ta funkcja jest wykonywana, gdy user wyśle jakieś polecenie do
         * serwera.
         */
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
            // Cast to a String first.
            // We know it is a String because we put some codec in TelnetPipelineFactory.
            String request = (String) e.getMessage();

            // Generate and write a response.
            String response = "Unknown command: " + request;
            boolean close = false;
            String[] parts = request.trim().split(" ");
            if (parts.length > 0) {
                if (parts[0].equals("login")) {
                    if (parts.length > 1 && parts[1].equals(loginText)) {
                        response = "You are now logged in.";
                        loggedIn.set(true);
                    } else {
                        response = "Bad login or password.";
                    }
                } else if (parts[0].equals("quit")) {
                    response = "Bye!";
                    close = true;
                } else if (parts[0].equals("stop")) {
                    if (loggedIn.get()) {
                        response = "Shutting server down.";
                        App.getApp().getLifecycle().shutdown();
                        close = true;
                    } else {
                        response = "You must log in to execute this command.";
                    }
                }
            }

            // We do not need to write a ChannelBuffer here.
            // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
            ChannelFuture future = e.getChannel().write(response + "\r\n");

            // Close the connection after sending 'Have a good day!'
            // if the client has sent 'bye'.
            if (close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }

        /**
         * Ta metoda jest wykonywana podczas błędu.
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            Logger.getLogger(RemoteAdminServer.class.getName()).log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
            e.getChannel().close();
        }
    }
}
