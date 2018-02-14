/*
 * Copyright (C) 2014, 2017. Miroslav Wengner, Marcus Hirt
 * This HttpDynamicUnit.java  is part of robo4j.
 * module: robo4j-core
 *
 * robo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * robo4j is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with robo4j .  If not, see <http://www.gnu.org/licenses/>.
 */

package com.robo4j.socket.http.units;

import com.robo4j.ConfigurationException;
import com.robo4j.CriticalSectionTrait;
import com.robo4j.LifecycleState;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.configuration.Configuration;
import com.robo4j.socket.http.channel.InboundSocketChannelHandler;
import com.robo4j.socket.http.dto.ServerUnitPathDTO;
import com.robo4j.socket.http.util.CodeRegistryUtils;
import com.robo4j.socket.http.util.HttpPathUtils;
import com.robo4j.socket.http.util.JsonUtil;
import com.robo4j.socket.http.util.RoboHttpUtils;

import java.util.List;

import static com.robo4j.socket.http.util.ChannelBufferUtils.INIT_BUFFER_CAPACITY;
import static com.robo4j.socket.http.util.RoboHttpUtils.HTTP_CODEC_PACKAGES;
import static com.robo4j.socket.http.util.RoboHttpUtils.HTTP_CODEC_REGISTRY;
import static com.robo4j.socket.http.util.RoboHttpUtils.HTTP_PROPERTY_PORT;
import static com.robo4j.socket.http.util.RoboHttpUtils.HTTP_UNIT_PATHS_CONFIG;
import static com.robo4j.socket.http.util.RoboHttpUtils.PROPERTY_BUFFER_CAPACITY;

/**
 * Http NIO unit allows to configure format of the requests currently is only
 * GET method available
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
@CriticalSectionTrait
public class HttpServerUnit extends RoboUnit<Object> {

	private final ServerContext serverContext = new ServerContext();
	private InboundSocketChannelHandler inboundSocketChannelHandler;
	private List<ServerUnitPathDTO> paths;

	public HttpServerUnit(RoboContext context, String id) {
		super(Object.class, context, id);
	}

	@Override
	protected void onInitialization(Configuration configuration) throws ConfigurationException {
		int port = configuration.getInteger(HTTP_PROPERTY_PORT, RoboHttpUtils.DEFAULT_PORT);
		int bufferCapacity = configuration.getInteger(PROPERTY_BUFFER_CAPACITY, INIT_BUFFER_CAPACITY);

		paths = JsonUtil.readPathConfig(ServerUnitPathDTO.class, configuration.getString(HTTP_UNIT_PATHS_CONFIG, null));

		serverContext.putProperty(PROPERTY_BUFFER_CAPACITY, bufferCapacity);
		serverContext.putProperty(HTTP_PROPERTY_PORT, port);

		String packages = configuration.getString(HTTP_CODEC_PACKAGES, null);

		serverContext.putProperty(HTTP_CODEC_REGISTRY, CodeRegistryUtils.getCodecRegistry(packages));

	}

	/**
	 * start updates context by references
	 */
	@Override
	public void start() {
		setState(LifecycleState.STARTING);
		HttpPathUtils.updateHttpServerContextPaths(getContext(), serverContext, paths);
		inboundSocketChannelHandler = new InboundSocketChannelHandler(getContext(), serverContext);
		inboundSocketChannelHandler.start();
		setState(LifecycleState.STARTED);
	}

	@Override
	public void stop() {
		setState(LifecycleState.STOPPING);
		inboundSocketChannelHandler.stop();
		setState(LifecycleState.STOPPED);
	}
}
