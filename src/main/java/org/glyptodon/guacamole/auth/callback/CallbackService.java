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

package org.glyptodon.guacamole.auth.callback;

import org.glyptodon.guacamole.auth.callback.conf.ConfigurationService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Credentials;
import org.glyptodon.guacamole.auth.callback.user.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for retrieving UserData objects through invoking an arbitrary HTTP
 * callback.
 */
@Singleton
public class CallbackService {

    /**
     * Logger for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(ConfigurationService.class);

    /**
     * Jersey REST client.
     */
    @Inject
    private Client client;

    /**
     * Service for retrieving configuration information regarding the
     * CallbackAuthenticationProvider.
     */
    @Inject
    private ConfigurationService confService;

    /**
     * Copies all parameter values from the given HTTPServletRequest to a new
     * Jersey WebResource, using the given WebResource as a basis.
     *
     * @param request
     *     The HttpServletRequest to copy parameters from.
     *
     * @param resource
     *     The WebResource to use as a basis.
     *
     * @return
     *     A new WebResource identical to the provided WebResource, but with
     *     query parameters copied from the given HttpServletRequest.
     */
    @SuppressWarnings("unchecked") // getParameterMap() is defined as returning Map<String, String[]>
    private WebResource copyParameters(HttpServletRequest request, WebResource resource) {

        // Get explicitly-typed parameter map
        Map<String, String[]> parameterMap = (Map<String, String[]>)
                request.getParameterMap();

        // For each parameter
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {

            // Add each name/value pair
            String name = entry.getKey();
            for (String value : entry.getValue())
                resource = resource.queryParam(name, value);

        }

        return resource;

    }

    /**
     * Retrieves a new UserData object by invoking the pre-configured HTTP
     * callback with the parameters within the given Credentials. If the HTTP
     * callback fails, or no UserData can be retrieved and there is no default
     * UserData available, null is returned.
     *
     * @param credentials
     *     The Credentials which should be passed to the HTTP callback.
     *
     * @return
     *     A new UserData object derived from the data returned by the HTTP
     *     callback, the default UserData object if the HTTP callback succeeded
     *     but returned no data, or null if no such data is available at all or
     *     the callback failed.
     *
     * @throws GuacamoleException
     *     If required properties are missing from guacamole.properties, or
     *     provided properties could not be parsed.
     */
    public UserData retrieveUserData(Credentials credentials)
            throws GuacamoleException {

        // Use default UserData if we are only mocking service responses
        if (confService.useMockService())
            return confService.getDefaultResponse();

        // Otherwise, use defined HTTP callback
        try {

            // Create WebResource for arbitrary callback
            WebResource resource = client.resource(confService.getCallbackURI());

            // Copy parameters from credential request, if available
            HttpServletRequest request = credentials.getRequest();
            if (request != null)
                resource = copyParameters(request, resource);

            // Attempt to retrieve UserData
            ClientResponse response =
                    resource.accept(MediaType.MEDIA_TYPE_WILDCARD)
                            .post(ClientResponse.class);

            // Determine status of response
            switch (response.getClientResponseStatus().getFamily()) {

                // Return nothing if the callback reported an error
                case CLIENT_ERROR:
                case SERVER_ERROR:
                    return null;

                // If the callback reported success, attempt to parse the
                // response
                case SUCCESSFUL:
                    return response.getEntity(UserData.class);
                    
            }

        }

        // It is expected that simple services will not bother with returning
        // user data JSON, but will instead rely on the default response
        catch (ClientHandlerException e) {
            logger.debug("Callback response was not valid user data JSON.", e);
        }

        // If callback did not return valid JSON, use default (if available)
        return confService.getDefaultResponse();

    }

}
