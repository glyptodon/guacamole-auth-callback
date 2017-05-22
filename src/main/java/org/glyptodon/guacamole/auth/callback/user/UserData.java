/*
 * Copyright (C) 2017 Glyptodon, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.glyptodon.guacamole.auth.callback.user;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * All data associated with a particular user.
 */
public class UserData {

    /**
     * The username of the user associated with this data. By default, the
     * anonymous user's username is used.
     */
    private String username = AuthenticatedUser.ANONYMOUS_IDENTIFIER;

    /**
     * All connections accessible by this user. The key of each entry is both
     * the connection identifier and the connection name.
     */
    private ConcurrentMap<String, Connection> connections;

    /**
     * The data associated with a Guacamole connection stored within a UserData
     * object.
     */
    public static class Connection {

        /**
         * The protocol that this connection should use, such as "vnc" or "rdp".
         */
        private String protocol;

        /**
         * Map of all connection parameter values, where each key is the parameter
         * name. Legal parameter names are dictated by the specified protocol and
         * are documented within the Guacamole manual:
         *
         * http://guac-dev.org/doc/gug/configuring-guacamole.html#connection-configuration
         */
        private Map<String, String> parameters;

        /**
         * Returns the protocol that this connection should use, such as "vnc"
         * or "rdp".
         *
         * @return
         *     The name of the protocol to use, such as "vnc" or "rdp".
         */
        public String getProtocol() {
            return protocol;
        }

        /**
         * Sets the protocol that this connection should use, such as "vnc"
         * or "rdp".
         *
         * @param protocol
         *     The name of the protocol to use, such as "vnc" or "rdp".
         */
        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        /**
         * Returns a map of all parameter name/value pairs, where the key of
         * each entry in the map is the corresponding parameter name. Changes
         * to this map directly affect the parameters associated with this
         * connection.
         *
         * @return
         *     A map of all parameter name/value pairs associated with this
         *     connection.
         */
        public Map<String, String> getParameters() {
            return parameters;
        }

        /**
         * Replaces all parameters associated with this connection with the
         * name/value pairs in the provided map, where the key of each entry
         * in the map is the corresponding parameter name. Changes to this map
         * directly affect the parameters associated with this connection.
         *
         * @param parameters
         *     The map of all parameter name/value pairs to associate with this
         *     connection.
         */
        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }

    }

    /**
     * Returns the username of the user associated with the data stored in this
     * object.
     *
     * @return
     *     The username of the user associated with the data stored in this
     *     object.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user associated with the data stored in this
     * object.
     *
     * @param username
     *     The username of the user to associate with the data stored in this
     *     object.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns all connections stored within this UserData object as an
     * unmodifiable map. Each of these connections is accessible by the user
     * specified by getUsername(). The key of each entry within the map is the
     * identifier and human-readable name of the corresponding connection.
     *
     * @return
     *     An unmodifiable map of all connections stored within this
     *     UserData object, where the key of each entry is the identifier of
     *     the corresponding connection.
     */
    public Map<String, Connection> getConnections() {
        return connections == null ? null : Collections.unmodifiableMap(connections);
    }

    /**
     * Replaces all connections stored within this UserData object with the
     * given connections. Each of these connections will be accessible by the
     * user specified by getUsername(). The key of each entry within the map is
     * the identifier and human-readable name of the corresponding connection.
     *
     * @param connections
     *     A map of all connections to be stored within this UserData object,
     *     where the key of each entry is the identifier of the corresponding
     *     connection.
     */
    public void setConnections(Map<String, Connection> connections) {
        this.connections = new ConcurrentHashMap<String, Connection>(connections);
    }

}
