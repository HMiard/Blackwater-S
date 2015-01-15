<?php

namespace DemoServer;

use Blackwater\Servers\BlackwaterBasicServer;
use Ratchet\ConnectionInterface;

class DemoServer extends BlackwaterBasicServer
{
    /**
     * Triggered when a client sends data through the socket.
     * This is the only method that must be overrided. The core logic for the server goes here.
     *
     * @param  \Ratchet\ConnectionInterface $from The socket/connection that sent the message to your application
     * @param  string $msg The message received
     * @throws \Exception
     */
    function onMessage(ConnectionInterface $from, $msg)
    {
        $this->sendToEveryoneBut($msg, array($from));
    }
}