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
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.net.auth.Credentials;
import org.glyptodon.guacamole.auth.callback.user.UserData;

/**
 * Service for retrieving UserData objects through invoking an arbitrary HTTP
 * callback.
 */
@Singleton
public class CallbackService {

    /**
     * Service for retrieving configuration information regarding the
     * CallbackAuthenticationProvider.
     */
    @Inject
    private ConfigurationService confService;

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

        // FIXME: STUB

        // If callback did not return valid JSON, use default (if available)
        return confService.getDefaultResponse();

    }

}
