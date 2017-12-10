/*
 * Copyright (c) 2014, 2017, Marcus Hirt, Miroslav Wengner
 *
 * Robo4J is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Robo4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.socket.http.channel;

import com.robo4j.socket.http.SocketException;
import com.robo4j.socket.http.dto.PathMethodDTO;
import com.robo4j.socket.http.message.HttpRequestDescriptor;
import com.robo4j.socket.http.message.HttpResponseDescriptor;
import com.robo4j.socket.http.util.ChannelBufferUtils;
import com.robo4j.socket.http.util.ChannelUtil;
import com.robo4j.socket.http.util.HttpMessageBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.List;

/**
 *
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class OutboundChannelHandler implements SocketHandler {

	private ByteChannel byteChannel;
	private List<PathMethodDTO> targetUnitByMethodMap;
	private HttpRequestDescriptor message;
	private HttpResponseDescriptor responseDescriptor;

	public OutboundChannelHandler(List<PathMethodDTO> targetUnitByMethodMap, ByteChannel byteChannel,
			HttpRequestDescriptor message) {
		this.targetUnitByMethodMap = targetUnitByMethodMap;
		this.byteChannel = byteChannel;
		this.message = message;
	}

	// TODO: 12/10/17 (miro) -> think about correct Path
	@Override
	public void start() {
		final PathMethodDTO pathMethod = new PathMethodDTO(correctUnitPath(message.getPath()), message.getMethod(), null);
		if (targetUnitByMethodMap.contains(pathMethod)) {
			// final ByteBuffer buffer = processMessageToClient(message);

			String resultMessage = HttpMessageBuilder.Build()
					.setDenominator(message.getDenominator())
					.addHeaderElements(message.getHeader())
					.build(message.getMessage());

			final ByteBuffer buffer = ChannelBufferUtils.getByteBufferByString(resultMessage);
			ChannelUtil.handleWriteChannelAndBuffer("client send message", byteChannel, buffer);
			responseDescriptor = getResponseDescriptor(byteChannel, pathMethod);
		}
	}

	@Override
	public void stop() {
		try {
			byteChannel.close();
		} catch (Exception e) {
			throw new SocketException("closing channel problem", e);
		}
	}

	public HttpResponseDescriptor getResponseDescriptor() {
		return responseDescriptor;
	}

	private String correctUnitPath(String path){
		return path.replace("units/", "");
	}

	private HttpResponseDescriptor getResponseDescriptor(ByteChannel byteChannel, PathMethodDTO pathMethod){
		try{
			HttpResponseDescriptor result = ChannelBufferUtils.getHttpResponseDescriptorByChannel(byteChannel);;
			//@formatter:off
			targetUnitByMethodMap
					.stream()
					.filter(e -> e.equals(pathMethod))
					.findFirst()
					.ifPresent(e -> result.setCallbackUnit(e.getCallbackUnitName()));
			//@formatter:on
			return result;
		} catch (IOException e){
			throw new SocketException("message body write problem", e);
		}
	}

}
