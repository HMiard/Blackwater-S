<?php

namespace Blackwater\Servers;

use Blackwater\Objects\User;
use Ratchet\ConnectionInterface;
use Ratchet\MessageComponentInterface;

/**
 * Convenient wrapper class for basic servers.
 * Note that all the Blackwater servers must follow the naming convention :
 * $serverName."Server"
 *
 * Example : the class for a server named "Air" should be :
 * AirServer
 *
 * Class BlackwaterBasicServer
 * @package Blackwater\Servers
 */
abstract class BlackwaterBasicServer extends BlackwaterBaseServer implements MessageComponentInterface
{

    function __construct($serverName){
        parent::__construct($serverName);
    }

    /**
     * When a new connection is opened it will be passed to this method
     * @param  ConnectionInterface $conn The socket/connection that just connected to your application
     * @throws \Exception
     */
    function onOpen(ConnectionInterface $conn)
    {
        $this->attachUser(new User($conn));
    }

    /**
     * This is called before or after a socket is closed (depends on how it's closed).  SendMessage to $conn will not result in an error if it has already been closed.
     * @param  ConnectionInterface $conn The socket/connection that is closing/closed
     * @throws \Exception
     */
    function onClose(ConnectionInterface $conn)
    {
        $this->findAndDetachUser($conn);
    }

    /**
     * If there is an error with one of the sockets, or somewhere in the application where an Exception is thrown,
     * the Exception is sent back down the stack, handled by the Server and bubbled back up the application through this method
     * @param  ConnectionInterface $conn
     * @param  \Exception $e
     * @throws \Exception
     */
    function onError(ConnectionInterface $conn, \Exception $e)
    {
        $this->say("An error occured : {$e->getMessage()}");
        $this->findAndDetachUser($conn);
        $conn->close();
    }

    /**
     * Triggered when a client sends data through the socket.
     * This is the only method that must be overrided. The core logic for the server goes here.
     *
     * @param  \Ratchet\ConnectionInterface $from The socket/connection that sent the message to your application
     * @param  string $msg The message received
     * @throws \Exception
     */
    abstract function onMessage(ConnectionInterface $from, $msg);


}