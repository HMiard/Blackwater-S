<?php

namespace Blackwater\Factories;

/**
 * Generic methods for all QueryFactories.
 * Please note that the class name for a QueryFactory attached to a server must follow this naming convention :
 * $serverName."QueryFactory"
 *
 * Example : if you want to attach a QueryFactory to your "Air" server, name it
 * AirQueryFactory
 *
 * QueryFactories should also be placed in the same namespace as their bound server.
 *
 * Class QueryFactory
 * @package Blackwater\Factories
 */
class QueryFactory
{
    /**
     * @var DBInterface
     */
    public $dbi;

    public function __construct(DBInterface $dbi){
        $this->dbi = $dbi;
    }

    /**
     * General debug utility function
     *
     * @param $something
     */
    public function say($something){
        $date = new \DateTime();
        echo "\n$ ".$date->format(DATE_COOKIE)." > ".$this->dbi->serverName." QueryFactory > ".$something;
    }
}