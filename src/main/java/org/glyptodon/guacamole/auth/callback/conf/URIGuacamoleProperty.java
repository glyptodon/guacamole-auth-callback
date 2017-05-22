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

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleServerException;
import org.apache.guacamole.properties.GuacamoleProperty;

/**
 * A GuacamoleProperty whose value is a URI.
 */
public abstract class URIGuacamoleProperty implements GuacamoleProperty<URI> {

    @Override
    public URI parseValue(String value) throws GuacamoleException {

        // If no property provided, return null.
        if (value == null)
            return null;

        // Parse string as URI
        try {
            return new URI(value);
        }

        // Rethrow failure to parse as a GuacamoleException
        catch (URISyntaxException e) {
            throw new GuacamoleServerException("Property \"" + getName()
                    + "\" must be a valid URI.", e);
        }

    }

}
