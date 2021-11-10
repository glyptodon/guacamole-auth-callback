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

package org.glyptodon.guacamole.auth.callback.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.BooleanGuacamoleProperty;
import org.glyptodon.guacamole.auth.callback.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for retrieving configuration information regarding the callback
 * authentication provider.
 */
public class ConfigurationService {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    /**
     * The Guacamole server environment.
     */
    @Inject
    private Environment environment;

    /**
     * ObjectMapper for serializing/deserializing JSON.
     */
    @Inject
    private ObjectMapper mapper;

    /**
     * The filename of the JSON file within GUACAMOLE_HOME which should be used
     * if the HTTP callback does not return its own data.
     */
    private static final String DEFAULT_USERDATA_FILENAME =
            "callback-default-response.json";

    /**
     * The property which defines the HTTP or HTTPS URI which should be used as
     * the authentication callback.
     */
    private static final URIGuacamoleProperty CALLBACK_AUTH_URI =
            new URIGuacamoleProperty() {

        @Override
        public String getName() {
            return "callback-auth-uri";
        }

    };

    /**
     * The property which defines whether a mock authentication callback
     * service should be used, rather than making actual HTTP requests to the
     * configured callback URI.
     */
    private static final BooleanGuacamoleProperty CALLBACK_USE_MOCK_SERVICE =
            new BooleanGuacamoleProperty() {

        @Override
        public String getName() {
            return "callback-use-mock-service";
        }

    };

    /**
     * Returns the HTTP or HTTPS URI which should be used as the authentication
     * callback.
     *
     * @return
     *     The HTTP or HTTPS URI which should be used as the authentication
     *     callback.
     *
     * @throws GuacamoleException
     *     If the "callback-auth-uri" property is missing or contains an
     *     invalid URI.
     */
    public URI getCallbackURI() throws GuacamoleException {
        return environment.getRequiredProperty(CALLBACK_AUTH_URI);
    }

    /**
     * Returns whether a mock authentication callback service should be used,
     * rather than making actual HTTP requests to the configured callback URI.
     * The mock callback service simply returns the default user data, if
     * available, and fails authentication if no default user data is provided.
     *
     * @return
     *     true if a mock authentication callback service should be used, false
     *     otherwise.
     *
     * @throws GuacamoleException
     *     If the "callback-use-mock-service" property could not be parsed.
     */
    public boolean useMockService() throws GuacamoleException {
        return environment.getProperty(CALLBACK_USE_MOCK_SERVICE, false);
    }

    /**
     * Returns a new UserData object representing the data which should be
     * exposed to any authenticated user for whom the arbitrary HTTP callback
     * does not return specific data.
     *
     * @return
     *     A new UserData object representing the data which should be exposed
     *     to authenticated users by default, if the arbitrary HTTP callback
     *     returned a successful response which did not contain specific data,
     *     or null if no such data is available.
     */
    public UserData getDefaultResponse() {

        File defaultResponse = new File(environment.getGuacamoleHome(),
                DEFAULT_USERDATA_FILENAME);

        // Do nothing if file does not appear to exist
        if (!defaultResponse.exists()) {
            logger.debug("Default response file \"{}\" does not appear to "
                    + "exist. Assuming no default.", defaultResponse);
            return null;
        }

        // Attempt to parse file
        try {
            return mapper.readValue(defaultResponse, UserData.class);
        }

        // Return no data if file could not be parsed
        catch (IOException e) {
            logger.info("Could not read default response from \"{}\": {}", defaultResponse, e.getMessage());
            logger.debug("Failed to read default response file \"{}\".", defaultResponse, e);
            return null;
        }

    }

}
