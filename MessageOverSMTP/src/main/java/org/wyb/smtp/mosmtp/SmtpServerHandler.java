/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.wyb.smtp.mosmtp;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Base64;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a server-side channel.
 */
@Sharable
public class SmtpServerHandler extends SimpleChannelInboundHandler<String> {
	
	private final MessageHandler messageHandler;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Send greeting for a new connection.
		ctx.write("220 " + InetAddress.getLocalHost().getHostName() + " at your service.\r\n");
		ctx.flush();
	}
	
	public SmtpServerHandler(MessageHandler messageHandler){
		this.messageHandler = messageHandler;
	}

	private StringBuffer messageBody;
	private boolean isMessageBody = false;
	private boolean isContent = false;
	private Message message;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
		//System.out.println("request="+request);
		// Generate and write a response.
		String response;
		boolean close = false;
		if (isMessageBody) {
			if(".".equals(request)){
				message.setSubject(new String(Base64.getDecoder().decode(message.getSubject()),"UTF-8"));
				message.setContent(new String(Base64.getDecoder().decode(messageBody.toString()),"UTF-8"));
				System.out.println(message.getSubject());
				System.out.println(message.getContent());
				System.out.println(message.getMessageId());
				System.out.println(message.getTime());
				isMessageBody = false;
				isContent = false;
				response = "250 OK";
				this.messageHandler.handle(message);
			}else{
				if(isContent){
					if(!request.isEmpty())messageBody.append(request);
				}else{
					if(request.startsWith("Message-ID: ")){
						message.setMessageId(request.substring(13, request.length()-1));
					}else if(request.startsWith("Date: ")){
						final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss, Z");
						message.setTime(sdf.parse(request.substring(6)));
					}else if(request.startsWith("Subject: ")){
						message.setSubject(request.substring(9).split("\\?")[3]);
					}else if(request.isEmpty()){
						isContent = true;
					}
				}
				return;
			}
			
		} else if (request.startsWith("HELO ")) {
			response = "250 Hello " + InetAddress.getLocalHost().getHostName() + ", I am glad to meet you";
		} else if (request.startsWith("MAIL FROM:") || request.startsWith("RCPT TO:")) {
			response = "250 Ok";
		} else if ("DATA".equals(request)) {
			messageBody = new StringBuffer();
			message = new Message();
			response = "354 End data with <CR><LF>.<CR><LF>";
			isMessageBody = true;
		} else if ("QUIT".equals(request)) {
			response = "221 Bye";
			close = true;
		} else {
			response = "502 Sorry, I can't do it";
			System.err.println(request);
		}

		// We do not need to write a ChannelBuffer here.
		// We know the encoder inserted at TelnetPipelineFactory will do the
		// conversion.
		//System.out.println(response);
		ChannelFuture future = ctx.write(response+"\r\n");

		// Close the connection after sending 'Have a good day!'
		// if the client has sent 'bye'.
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		System.err.println("close");
	}
}
