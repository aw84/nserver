package pl.training.poc.http2;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Settings;

public class HelloWorldHttp2HandlerBuilder
		extends AbstractHttp2ConnectionHandlerBuilder<HelloWorldHttp2Handler, HelloWorldHttp2HandlerBuilder> {

	public HelloWorldHttp2HandlerBuilder() {
	}

	@Override
	public HelloWorldHttp2Handler build() {
		return super.build();
	}

	@Override
	protected HelloWorldHttp2Handler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder,
			Http2Settings initialSettings) {
		HelloWorldHttp2Handler handler = new HelloWorldHttp2Handler(decoder, encoder, initialSettings);
		frameListener(handler);
		return handler;
	}

}
