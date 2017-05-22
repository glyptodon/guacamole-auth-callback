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

import java.util.Map;
import org.apache.guacamole.net.auth.simple.SimpleConnection;
import org.apache.guacamole.protocol.GuacamoleConfiguration;

/**
 * Connection implementation which uses the configuration information from the
 * connection data of a UserData object.
 */
public class UserDataConnection extends SimpleConnection {

    /**
     * Generates a new GuacamoleConfiguration from the associated protocol and
     * parameters of the given UserData.Connection.
     *
     * @param connection
     *     The UserData.Connection whose protocol and parameters should be used
     *     to construct the new GuacamoleConfiguration.
     *
     * @return
     *     A new GuacamoleConfiguration generated from the associated protocol
     *     and parameters of the given UserData.Connection.
     */
    private static GuacamoleConfiguration getConfiguration(UserData.Connection connection) {

        // Create new configuration for given protocol
        GuacamoleConfiguration config = new GuacamoleConfiguration();
        config.setProtocol(connection.getProtocol());

        // Add all parameter name/value pairs
        Map<String, String> parameters = connection.getParameters();
        if (parameters != null)
            config.setParameters(parameters);

        return config;

    }

    /**
     * Creates a new UserDataConnection based on the given connection data.
     *
     * @param identifier
     *     The identifier associated with this connection within the associated
     *     UserData.
     *
     * @param connection
     *     The connection data associated with this connection.
     */
    public UserDataConnection(String identifier, UserData.Connection connection) {
        super(identifier, identifier, getConfiguration(connection));
    }

}
