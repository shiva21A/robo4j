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
package com.robo4j.socket.http.util;

import com.robo4j.socket.http.HttpHeaderNames;
import com.robo4j.socket.http.HttpMethod;
import com.robo4j.socket.http.enums.StatusCode;
import com.robo4j.socket.http.request.RoboBasicMapEntry;
import com.robo4j.socket.http.units.Constants;
import com.robo4j.util.StringConstants;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Basic Http constants and utils methods
 *
 * @author Miroslav Wengner (@miragemiko)
 */
public final class RoboHttpUtils {

	public static final String HTTP_VERSION = "HTTP/1.1";
	private static final String ROBO4J_CLIENT = "Robo4J-HttpClient";
	public static final String NEW_LINE = "\n";
	public static final CharSequence CHAR_QUOTATION_MARK = "\"";
	public static final CharSequence CHAR_COLON = ":";
	public static final CharSequence CHAR_CURLY_BRACKET_LEFT = "{";
	public static final CharSequence CHAR_CURLY_BRACKET_RIGHT = "}";
	public static final CharSequence CHAR_SQUARE_BRACKET_LEFT = "[";
	public static final CharSequence CHAR_SQUARE_BRACKET_RIGHT = "]";
	public static final CharSequence CHAR_COMMA = ",";
	public static final int _DEFAULT_PORT = 8042;
	public static final String HTTP_TARGET_UNITS = "targetUnits";

	public static String setHeader(String responseCode, int length) throws IOException {
		//@formatter:off
		return HttpHeaderBuilder.Build()
				.add(StringConstants.EMPTY, responseCode)
				.add(HttpHeaderNames.DATE, LocalDateTime.now().toString())
				.add(HttpHeaderNames.SERVER, ROBO4J_CLIENT)
				.add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(length))
				.add(HttpHeaderNames.CONTENT_TYPE, "text/html;".concat(Constants.UTF8_SPACE).concat("charset=utf-8"))
				.build();
		//@formatter:on
	}

	public static String createHeaderWithFirstLineAndMap(String first, Map<String, String> headerMap) {
		HttpHeaderBuilder result = HttpHeaderBuilder.Build();
		headerMap.forEach(result::add);
		return first.concat(result.build());

	}

	public static String createResponseWithHeaderAndMessage(String header, String message){
		return header.concat(NEW_LINE).concat(message);
	}

	public static String createResponseByCode(StatusCode code){
		return  RoboHttpUtils.createResponseWithHeaderAndMessage(
				RoboResponseHeader.headerByCode(code),
				StringConstants.EMPTY);
	}

	public static String createRequestHeader(String host, int length) {
		//@formatter:off
		HttpHeaderBuilder builder = HttpHeaderBuilder.Build()
			.add(HttpHeaderNames.HOST, host)
			.add(HttpHeaderNames.CONNECTION, "keep-alive")
			.add(HttpHeaderNames.CACHE_CONTROL, "no-cache")
			.add(HttpHeaderNames.USER_AGENT, ROBO4J_CLIENT)
			.add(HttpHeaderNames.ACCEPT, "*/*")
			.add(HttpHeaderNames.ACCEPT_ENCODING, "gzip, deflate, sdch, br")
			.add(HttpHeaderNames.ACCEPT_LANGUAGE, "en-US,en;q=0.8")
			.add(HttpHeaderNames.CONTENT_TYPE, "text/html;".concat(Constants.UTF8_SPACE).concat("charset=utf-8"));
		if(length != 0){
			builder.add(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(length));
		}
		return builder.build();
		//@formatter:on
	}

	public static String createRequest(HttpMethod method, String host, String uri, String message) {
		//@formatter:off
		final String header = createHeader(method, host, uri, message);
		return createRequest(header, message);
		//@formatter:on
	}

	public static String createRequest(String header, String message) {
		//@formatter:off
		return header
				.concat(NEW_LINE)
				.concat(message);
		//@formatter:on
	}

	public static String createHeader(HttpMethod method, String host, String uri, String message) {
		return createHeaderFirstLine(method, uri).concat(createRequestHeader(host, message.length()));
	}

	public static String createHeaderFirstLine(HttpMethod method, String uri) {
		return HttpFirstLineBuilder.Build(method.getName()).add(uri).add(HTTP_VERSION).build();
	}

	public static String createGetRequest(String host, String message) {
		//@formatter:off
		return HttpFirstLineBuilder.Build(HttpMethod.GET.getName()).add(message).add(HTTP_VERSION)
				.build().concat(createRequestHeader(host, 0));
		//@formatter:on
	}

	public static String correctLine(String line) {
		return line == null ? StringConstants.EMPTY : line;
	}

	public static Map<String, String> parseURIQueryToMap(final String uriQuery, final String delimiter) {
		//@formatter:off
		return Stream.of(uriQuery.split(delimiter))
				.filter(e -> !e.isEmpty())
				.map(RoboBasicMapEntry::new)
				.collect(Collectors.toMap(RoboBasicMapEntry::getKey, RoboBasicMapEntry::getValue));
		//@formatter:on
	}

}
