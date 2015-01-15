![Blackwater-S Logo](https://cloud.githubusercontent.com/assets/5138926/5761991/613bc3a6-9cdf-11e4-826d-fb91557e248e.png)

Description
==============

**Blackwater Server-Interface** is a multiplatform desktop application providing
a simple, easy to use GUI, allowing the user to quickly create, manage and scale
websockets PHP servers.

![Blackwater-S interface](https://cloud.githubusercontent.com/assets/5138926/5761971/4b419e2c-9cdf-11e4-8dc3-9c1ef0baf4a9.png)

Generated Blackwater projects are based on **Blackwater-P**, a side-project
available from packagist (<https://packagist.org/packages/blackwater/blackwaterp>).
Blackwater-P extends basic Ratchet/WAMP based servers, allowing a persistent
database connection availability and wrapping some useful methods.

In order to use Blackwater-S, you will need PHP (>5.3.9) accessible
from a terminal, and a connection to a database of your choice
(MySQL by default).

See Ratchet at <http://socketo.me/>


Binary distributions
==============

*Coming soon*


Building from source
==============

In order to build Blackwater-S, install Apache Ant and run "ant" in the root of
the cloned local repository.

Building Blackwater-S from source will result in a folder containing
resources and the bootstrap jar. Java 1.8.0_25 at least is needed,
or you will need to adapt the source-code.

**IMPORTANT** : the ant build uses IntelliJ IDEA's javac2, so you will need
to install IntelliJ and update the *idea.home* constant in the build.properties
file, or download the javac2 jar and change the following line in the build.xml
file :
``<property name="javac2.home" value="${idea.home}/lib"/>``


Quick documentation
==============

*Coming soon*
