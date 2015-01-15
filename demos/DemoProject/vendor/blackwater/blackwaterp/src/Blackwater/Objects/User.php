<?php

namespace Blackwater\Objects;
use Ratchet\ConnectionInterface;


/**
 * Very basic websocket-connected user class.
 *
 * Class User
 * @package Blackwater\Objects
 */
class User
{
    /**
     * @var ConnectionInterface
     */
    private $conn;
    /**
     * @var \DateTime
     */
    private $connectionDate;


    public function __construct(ConnectionInterface $conn){

        $this->conn = $conn;
        $this->connectionDate = new \DateTime();
    }

    /**
     * @return ConnectionInterface
     */
    public function getConn()
    {
        return $this->conn;
    }

    /**
     * @return \DateTime
     */
    public function getConnectionDate()
    {
        return $this->connectionDate;
    }
}