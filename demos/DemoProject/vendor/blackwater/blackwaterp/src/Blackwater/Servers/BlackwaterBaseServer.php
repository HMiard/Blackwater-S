<?php

namespace Blackwater\Servers;

use Blackwater\Kernel;
use Blackwater\Objects\User;
use Ratchet\ConnectionInterface;

/**
 * Base class for basics and advanced Blackwater servers.
 *
 * Class BlackwaterBaseServer
 * @package Blackwater\Server
 */
class BlackwaterBaseServer extends Kernel
{
    /**
     * @var \SplObjectStorage
     */
    public $users;


    public function __construct($serverName){

        parent::__construct($serverName);

        $this->users = new \SplObjectStorage();
    }


    public function attachUser(User $user){

        $this->users->attach($user);
        $this->say("New user connected !");
    }

    public function detachUser(User $user){

        $this->users->detach($user);
        $this->say("User detached from server.");
    }

    /**
     * Handy method.
     *
     * @param ConnectionInterface $conn
     */
    public function findAndDetachUser(ConnectionInterface $conn){

        $u = $this->getPlayerByConn($conn);
        if ($u !== false)
            $this->detachUser($u);
        else
            $this->say("Unable to find the user.");
    }

    /**
     * Sending a message to every single connected user.
     *
     * @param $data
     */
    public function sendToEveryone($data){

        /**
         * @var $u User
         */
        foreach ($this->users as $u)
            $u->getConn()->send($data);

        $this->say("Data sent to everyone : ".$data);
    }

    /**
     * Sending a message to everyone but specified connections.
     *
     * @param $data
     * @param $connections array
     */
    public function sendToEveryoneBut($data, array $connections){

        /**
         * @var $u User
         */
        foreach ($this->users as $u){
            if (!in_array($u->getConn(), $connections))
                $u->getConn()->send($data);
        }
        $this->say("Data sent to everyone but ... : ".$data);
    }

    /**
     * Using a Connection item to identify a player
     *
     * @param ConnectionInterface $conn
     * @return User
     */
    public function getPlayerByConn(ConnectionInterface $conn){

        foreach ($this->users as $u){
            if ($u->conn == $conn) return $u;
        }
        return false;
    }
}
