/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.base.Charsets;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

/**
 * Klient serwera TCPEBusServer. Jest to zdalna szyna, która wymienia dane z
 * szyną po stronie serwera.
 *
 * @author jblew
 */
public class NetworkBusClient extends EventBus {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    static {
        JSON_MAPPER.setSerializationConfig(JSON_MAPPER.getSerializationConfig().without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS));
    }
    private final ClientBootstrap bootstrap;
    private final Channel channel;

    public NetworkBusClient(SocketAddress addr) throws IOException {
        ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        bootstrap = new ClientBootstrap(factory);

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 0));
                pipeline.addLast("handler", new Handler());
                return pipeline;
            }
        });

        bootstrap.setOption("tcpNoDelay", true);
        bootstrap.setOption("keepAlive", true);
        try {
            channel = bootstrap.connect(addr).await().getChannel();
        } catch (InterruptedException ex) {
            throw new IOException("Connect interrupted");
        }
        if(channel.isBound() && channel.isConnected() && channel.isOpen()) {
            System.out.println("Connected");
        }
        else {
            throw new IOException("Not connected!");
        }
        if(!channel.isWritable()) throw new IOException("Socket is not writeable!");
        if(!channel.isReadable()) throw new IOException("Socket is not readable!");
    }

    public void close() throws IOException {
        channel.close();
        bootstrap.releaseExternalResources();
    }

    public void postOnlyOnLocalBus(Object o) {
        super.post(o);
    }

    @Override
    public void post(Object o) {
        postOnlyOnLocalBus(o);
        if (!(o instanceof DeadEvent)) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JSON_MAPPER.writeValue(baos, o);
                byte[] bytes = baos.toByteArray();
                byte[] classNameBytes = o.getClass().getName().getBytes();
                ChannelBuffer buf = ChannelBuffers.buffer(bytes.length + classNameBytes.length + 8);
                buf.writeInt(buf.capacity() - 4);
                buf.writeInt(classNameBytes.length);
                buf.writeBytes(classNameBytes);
                buf.writeBytes(bytes);

                channel.write(buf);
            } catch (Exception ex) {
                Logger.getLogger(TCPEBusServer.class.getName()).log(Level.SEVERE, "Exception in client.post", ex);
            }
        }
    }

    class Handler extends SimpleChannelUpstreamHandler {

        int waitingBytes = 0;

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
            try {
                //System.out.println("CLT.MSG_RECV");
                ChannelBuffer buf = (ChannelBuffer) e.getMessage();
                int length = buf.readInt();//4
                byte[] classNameBytes = new byte[buf.readInt()];//4
                buf.readBytes(classNameBytes);
                String className = new String(classNameBytes, Charsets.UTF_8);
                //System.out.println("CLT.ClassName: "+className);
                //System.out.println("length="+length+"; classNameBytes.length="+classNameBytes.length+";"
                //        + " contentLength="+(length-classNameBytes.length-4)+"; buf.capacity="+buf.capacity());
                byte[] bytes = new byte[length - classNameBytes.length - 4];
                buf.readBytes(bytes);
                try {
                    Object o = JSON_MAPPER.readValue(bytes, Class.forName(className));
                    //System.out.println(">>>Client received " + o.getClass() + " o.toString(): " + o.toString());
                    postOnlyOnLocalBus(o);
                } catch (ClassNotFoundException | IOException ex) {
                    Logger.getLogger(TCPEBusServer.class.getName()).log(Level.SEVERE, "Exception in client.receive", ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(TCPEBusServer.class.getName()).log(Level.SEVERE, "Exception in client.messageReceived", ex);
            }
        }

        @Override
        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            super.handleUpstream(ctx, e);
            //System.out.println("Event: " + e.toString() + " (" + e.getClass().getName() + ")");
        }

        @Override
        public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e) throws Exception {
            super.writeComplete(ctx, e);
            //System.out.println("Write complete");
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
