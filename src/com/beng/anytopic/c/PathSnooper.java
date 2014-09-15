package com.beng.anytopic.c;

import java.util.List;

import com.beng.anytopic.App;

import lombok.extern.slf4j.Slf4j;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;

@Slf4j
public class PathSnooper extends HttpObjectAggregator {

	public PathSnooper(int maxContentLength) {
		super(maxContentLength);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
		if(msg instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) msg;
			ctx.channel().attr(App.CHANNEL_PATH).set(req.getUri());
		}
		super.decode(ctx, msg, out);
	}
	
}
