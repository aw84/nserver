package pl.training.poc.http2;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class Http2Server {
	static final boolean SSL = System.getProperty("ssl") != null;

	static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));

	public static void main(String[] args) throws Exception {
		final SslContext sslCtx;
		if (SSL) {
			sslCtx = createSslContext();
		} else {
			sslCtx = null;
		}
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(group).channel(NioServerSocketChannel.class).childHandler(new Http2ServerInitializer(sslCtx));
			Channel ch = b.bind(PORT).sync().channel();
			ch.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	private static SslContext createSslContext() throws CertificateException, SSLException {
		final SslContext sslCtx;
		SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;
		SelfSignedCertificate ssc = new SelfSignedCertificate();
		sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).sslProvider(provider)
				/*
				 * NOTE: the cipher filter may not include all ciphers required by the HTTP/2
				 * specification. Please refer to the HTTP/2 specification for cipher
				 * requirements.
				 */
				.ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
				.applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
						// NO_ADVERTISE is currently the only mode supported by both OpenSsl and JDK
						// providers.
						SelectorFailureBehavior.NO_ADVERTISE,
						// ACCEPT is currently the only mode supported by both OpenSsl and JDK
						// providers.
						SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2
				// ,ApplicationProtocolNames.HTTP_1_1
				)).build();
		return sslCtx;
	}
}
