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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Robo4J. If not, see <http://www.gnu.org/licenses/>.
 */
package com.robo4j.configuration;

import com.robo4j.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * XML Tests for the configuration.
 * 
 * @author Marcus Hirt
 * @author Miroslav Wengner (@miragemiko)
 */
class XmlConfigurationFactoryTest {
	@Test
	void testSerializeToString() throws ConfigurationFactoryException {
		ConfigurationBuilder configBuilder = new ConfigurationBuilder().addString("firstString", "S1").addString("secondString", "S2").addBoolean("boolean", true).addBuilder("child1", new ConfigurationBuilder().addInteger("int", 1).addFloat("float", 1.0f)).addBuilder("child2", new ConfigurationBuilder().addInteger("int", 2).addFloat("float", 2.0f));
		Configuration config = configBuilder.build();
		String xml = XmlConfigurationFactory.toXml(config);
		System.out.println(xml);
		assertNotNull(xml);

		Configuration fromXml = XmlConfigurationFactory.fromXml(xml);
		System.out.println(XmlConfigurationFactory.toXml(fromXml));

		assertEquals(config, fromXml);
	}

	@Test
	void testReadResource() throws IOException, ConfigurationFactoryException {
		String configXml = IOUtil
				.readStringFromUTF8Stream(XmlConfigurationFactoryTest.class.getClassLoader().getResourceAsStream("configurationtest.xml"));
		Configuration config = XmlConfigurationFactory.fromXml(configXml);
		assertNotNull(config.getChildConfiguration("multipliers"));
		assertNotNull(config.getChildConfiguration("offsets"));
	}
}
