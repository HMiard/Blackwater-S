<?php

namespace ChatServer\utils;

use Blackwater\Objects\User;
use Ratchet\ConnectionInterface;

/**
 * Class MyUser
 * @package ChatServer\utils
 */
class ChatUser extends User {

    private $name;
    /**
     * @var ChatRoom
     */
    private $curentRoom;

    public function __construct(ConnectionInterface $conn, $pseudo){

        $this->name = $pseudo;
        parent::__construct($conn);
    }

    /**
     * @return mixed
     */
    public function getName()
    {
        return $this->name;
    }

    /**
     * @param mixed $name
     */
    public function setName($name)
    {
        $this->name = $name;
    }

    /**
     * @return ChatRoom
     */
    public function getCurentRoom()
    {
        return $this->curentRoom;
    }

    /**
     * @param ChatRoom $curentRoom
     */
    public function setCurentRoom($curentRoom)
    {
        $this->curentRoom = $curentRoom;
    }


}