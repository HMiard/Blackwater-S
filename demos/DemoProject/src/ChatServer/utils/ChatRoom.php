<?php

namespace ChatServer\utils;

use ChatServer\ChatServer;

class ChatRoom {

    /**
     * @var ChatServer
     */
    public $server;
    /**
     * @var \SplObjectStorage
     */
    public $users;
    public $name;

    public function __construct($name, ChatServer $server){
        $this->name = $name;
        $this->server = $server;
        $this->users = new \SplObjectStorage();
    }

    public function connectUser(ChatUser $user){

        if ($this->users->contains($user)) return;
        // Can't be in two rooms at the same time !
        $this->server->disconnectUserFromRooms($user);
        $this->users->attach($user);
        $user->setCurentRoom($this);
        $user->getConn()->send("You joined the ".$this->name);
        $this->sendMessage($user->getName() . " joined the room !");
    }


    public function disconnectUser(ChatUser $user){

        if (!$this->users->contains($user)) return;
        $this->users->detach($user);
        $user->getConn()->send("You left the ".$this->name);
        $this->sendMessage($user->getName() . " disconnected !");
    }


    /**
     * Relaying a message to every room user.
     *
     * @param ChatUser $user
     * @param $msg
     */
    public function relay(ChatUser $user, $msg){

        /**
         * @var $u ChatUser
         */
        foreach ($this->users as $u){
            if ($u != $user)
                $u->getConn()->send($user->getName() . " says : ".$msg);
        }
        $user->getConn()->send("You said : ".$msg);
    }


    public function sendMessage($message){

        /**
         * @var ChatUser $user
         */
        foreach ($this->users as $user){
            $user->getConn()->send("\nServer : ".$message);
        }
    }
}