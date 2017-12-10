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

package com.robo4j.socket.http.units.test;

import com.robo4j.AttributeDescriptor;
import com.robo4j.DefaultAttributeDescriptor;
import com.robo4j.RoboContext;
import com.robo4j.RoboUnit;
import com.robo4j.logging.SimpleLoggingUtil;
import com.robo4j.socket.http.codec.CameraMessage;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public class CameraImageConsumerTestUnit extends RoboUnit<CameraMessage> {
	public static final String ATTRIBUTE_NUMBER_OF_RECEIVED_IMAGES_NAME = "numberOfReceivedImages";
	public static final Collection<AttributeDescriptor<?>> ATTRIBUTES_COLLECTION = Collections
			.unmodifiableCollection(Collections.singletonList(
					DefaultAttributeDescriptor.create(Integer.class, ATTRIBUTE_NUMBER_OF_RECEIVED_IMAGES_NAME)));

	private final AtomicInteger counter = new AtomicInteger(0);

	public CameraImageConsumerTestUnit(RoboContext context, String id) {
		super(CameraMessage.class, context, id);
		System.out.println(getClass() + " Constructor");
	}

	@Override
	public void onMessage(CameraMessage message) {
		if (message.getImage() != null) {
			final byte[] bytes = Base64.getDecoder().decode(message.getImage());
			System.out.println(getClass().getSimpleName() + " Delivered image: " + counter.incrementAndGet() + " size: "
					+ bytes.length + " imageSize: " + message.getImage().length());
		} else {
			SimpleLoggingUtil.error(getClass(), "no imageView: " + counter.getAndIncrement());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <R> R onGetAttribute(AttributeDescriptor<R> descriptor) {
		if (descriptor.getAttributeType() == Integer.class
				&& descriptor.getAttributeName().equals(ATTRIBUTE_NUMBER_OF_RECEIVED_IMAGES_NAME)) {
			return (R) Integer.valueOf(counter.get());
		}
		return super.onGetAttribute(descriptor);
	}

}
