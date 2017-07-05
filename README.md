CopaServer
==========

License
-------
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

About
-----
CoPA (Change of Plan Application) is a notification system for schedule changes.
It can be connected to different user authentication systems and databases of events.
Event responsibles can easily notify their subscribers about changes via a webinterface and an Android app.
Subscribers to events receive these notifications over the same channels and by email.
This repository contains the server part of the system.

Overview
--------
The system is implemented as a RESTful webservice using a [Java servlet](src/main/java/unicopa/copa/server/servlet/CopaServlet.java). The possible requests to this service are defined as subclasses of `AbstractRequest`, to be found in the [CopaBase](https://github.com/UniCopa/CopaBase) repository. Requests are handled by the corresponding subclasses of [RequestHandler](src/main/java/unicopa/copa/server/com/requestHandler/RequestHandler.java) in the [requestHandler](src/main/java/unicopa/copa/server/com/requestHandler/) package. Access to the database is provided via a [DatabaseService](src/main/java/unicopa/copa/server/database/DatabaseService.java) and LDAP user registration is handled by class [Registration](src/main/java/unicopa/copa/server/Registration.java). [Email](src/main/java/unicopa/copa/server/notification/EmailNotificationService.java) and [Google Cloud Messaging](src/main/java/unicopa/copa/server/notification/GoogleCloudNotificationService.java) notification services are implemented as subclasses of [NotificationService](src/main/java/unicopa/copa/server/notification/GoogleCloudNotificationService.java). The servlet, as the application's entry point, uses the [CopaSystem](src/main/java/unicopa/copa/server/CopaSystem.java) class where all functionality comes together.

Documentation
-------------
See the [Java API documentation](http://unicopa.github.io/CopaServer/).
