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

## Windows :
<https://sourceforge.net/projects/blackwaters/files/latest/download?source=files>

## Linux :
*Coming soon*

## Mac :
*Coming soon*

Building from source
==============

In order to build Blackwater-S, simply use the following command in the project
root directory :

```bash
gradle dist
```

The application will be located in build/distributions/Blackwater-S.
NOTE : The installer will not install a working version of the application
for now.


Quick documentation
==============

## Running the demo

Once you are able to run Blackwater-S, either from the source-built jar
or from a binary distribution, click the "Open" button in the left panel.
Navigate to the folder where Blackwater-S is installed and select the
**demos/DemoProject** folder.

Since you are loading the project for the first time, Blackwater-S will
run *composer update*, disabling the project windows. Once the demo is
successfully loaded, you should see something like this :

![Demo-screen1](https://cloud.githubusercontent.com/assets/5138926/5860885/f809184e-a264-11e4-8239-531f5b6c4b06.png)

Click on the airplane icon to launch the ChatServer. If everything is okay,
the console should print something like
*Blackwater > Chat server running on localhost, port 8035 !*

Open then **DemoProject/src/ChatClient/index.html** in your favorite browser.
If the server is still running, you'll see a basic chat application, asking
you to chose a room and a name.
If you open other instances of the client in your browser, and connect with
different names, you'll be able to chat with yourself ! Wow.
(Press return to send text).

If you halt the server in Blackwater-S, using the cross icon or closing the
window, you'll notice that all the clients are instantly disconnected from
the application.


## Creating a basic Blackwater Server

*Coming soon*
