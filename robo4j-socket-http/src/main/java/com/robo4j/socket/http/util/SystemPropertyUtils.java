/*
 * Copyright (c) 2014-2019, Marcus Hirt, Miroslav Wengner
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
package com.robo4j.socket.http.util;

import java.util.Objects;

/**
 * Utility class helps to parse System Properties values
 *
 * @author Marcus Hirt (@hirt)
 * @author Miro Wengner (@miragemiko)
 */
public final class SystemPropertyUtils {

    public static String get(String key, String def){
        if(key == null){
            throw new NullPointerException("key");
        }
        Objects.requireNonNull(key, "key");
        if(key.isEmpty()){
            throw new IllegalArgumentException("key is empty");
        }

        String result = System.getProperty(key);
        return result != null ? result : def;
    }

}
