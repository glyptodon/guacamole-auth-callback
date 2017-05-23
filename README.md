guacamole-auth-callback
=======================

guacamole-auth-callback is an extension for [Apache
Guacamole](http://guacamole.incubator.apache.org) which authenticates users
against an arbitrary HTTP service. Whenever an authentication attempt is made,
the parameters submitted are copied and forwarded in an HTTP POST request to
the configured service. The response from that service dictates whether the
user is authenticated and, optionally, the data available to that user.

Building the extension
======================

guacamole-auth-callback is built using [Apache
Maven](http://maven.apache.org/). Assuming Maven is installed, building the
extension requires running only one command within the root of the source tree:

    $ mvn package

The resulting extension, `guacamole-auth-callback-0.9.12.jar` will be written
to the `target/` subdirectory, which Maven will create if it does not yet
exist. This `.jar` file must be copied to the `GUACAMOLE_HOME/extensions`
directory of the Guacamole server(s) to be installed.

Installation and configuration
==============================

To install guacamole-auth-callback on a Guacamole server, copy the
`guacamole-auth-callback-0.9.12.jar` file to `GUACAMOLE_HOME/extensions`, where
`GUACAMOLE_HOME` is the configuration directory for your deployment of
Guacamole, [as defined in the manual](http://guacamole.incubator.apache.org/doc/gug/configuring-guacamole.html#guacamole-home).

Once the extension is in place, [`guacamole.properties`](http://guacamole.incubator.apache.org/doc/gug/configuring-guacamole.html#initial-setup) will need to
be modified, defining the arbitrary authentication service to be used:

Property Name                | Description
---------------------------- | -----------
`callback-auth-uri`          | The URI of the authentication endpoint. This endpoint will receive a POST for every authentication attempt, and should return 200 status if the authentication attempt should be allowed. Error responses will be treated as rejections of the authentication attempt. The service may additionally return JSON defining the data available to the user, as described below. *If such JSON is not returned, the contents of `GUACAMOLE_HOME/callback-default-response.json` will be used instead.* The value of this property is ignored if `callback-use-mock-service` is set to `true`.
`callback-use-mock-service`  | Whether an internal, simulated authentication endpoint should be used instead of the defined authentication endpoint. If set to `true`, the contents of `GUACAMOLE_HOME/callback-default-response.json` will be used for all authentication attempts. If the `callback-default-response.json` file is missing, all authentication attempts will be rejected.

The default response
--------------------

If your authentication service does not always return data for authenticated
users, a default response may be provided within
`GUACAMOLE_HOME/callback-default-response.json`. This default response will be
used whenever the authentication service returns a successful status code, but
the contents of the response either lack JSON defining the user's data, or the
JSON present is not a valid definition of user data. If the default response is
not provided, any such case will simply result in the authentication attempt
being rejected.

User data format
----------------

The data associated with users authenticated via guacamole-auth-callback is
defined using JSON. This JSON is either returned by the authentication service
or defined within `GUACAMOLE_HOME/callback-default-response.json`. In either
case, the format of this JSON is the same:

    {
        "username" : "arbitraryUsername",
        "connections" : {
            "Connection Name" : {
                "protocol" : "vnc",
                "parameters" : {
                    "hostname" : "vnc-host",
                    "port" : "5901"
                }
            }
        }
    }

There are only two properties, both of which are optional: `username`, which
defines the username of the user which authenticated, and `connections`, which
defines the set of connections available to that user. If the `username`
property is omitted, the user will simply be anonymous (equivalent to specfying
the empty string for the `username`). If `connections` is omitted, no
connections will be defined by guacamole-auth-callback for that user (but
connections may be provided by other authentication mechanisms, such as the
[database authentication](http://guacamole.incubator.apache.org/doc/gug/jdbc-auth.html)).

Property Name | Type     | Description
------------- | -------- | -----------
`username`    | `string` | The authenticated user's username. If omitted, the user will simply be anonymous.
`connections` | `object` | The set of all connections available to the user. If omitted, no connections will be defined by guacamole-auth-callback for that user, but other installed extensions may provide connections if they honor guacamole-auth-callback's authentication result.

Connections within the `connections` property are defined using two properties
each: `protocol`, which must be the name of the protocol that guacd should use
to establish the connection (such as "vnc" or "rdp), and `parameters`, which
must be a set of connection parameter name/value pairs [as documented for the
specified protocol in the manual](http://guacamole.incubator.apache.org/doc/gug/configuring-guacamole.html#connection-configuration).

Property Name | Type     | Description
------------- | -------- | -----------
`protocol`    | `string` | The unique name of the protocol to be used by guacd to establish the remote desktop connection, such as "vnc" or "rdp".
`parameters`  | `object` | The set of all connection parameter name/value pairs to apply to the connection,  [as documented for the specified protocol in the manual](http://guacamole.incubator.apache.org/doc/gug/configuring-guacamole.html#connection-configuration).

Finalizing the install
----------------------

Once the `.jar` file is in place, `guacamole.properties` has been edited
appropriately, and `callback-default-response.json` has been written (if
required), Tomcat must be restarted for the extension to be loaded and its new
configuration to be read.

If all has been done correctly, you should see a message in the Tomcat logs
noting that the extension has been loaded:

    INFO  o.a.g.extension.ExtensionModule - Extension "HTTP Callback Authentication" loaded.

