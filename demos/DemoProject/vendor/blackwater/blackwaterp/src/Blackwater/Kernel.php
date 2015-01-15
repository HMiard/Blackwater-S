<?php

namespace Blackwater;

use Blackwater\Factories\DBInterface;
use Blackwater\Factories\QueryFactory;

require_once __DIR__ . '/conf/db.php';

/**
 * Core class for extended blackwater servers.
 *
 * Class Kernel
 * @package Blackwater
 */
class Kernel
{
    /**
     * @var DBInterface
     */
    public $dbi;
    /**
     * @var String
     */
    public $serverName;
    /**
     * @var array
     */
    public $serverDescr;
    /**
     * @var QueryFactory
     */
    public $queryFactory;



    public function __construct($serverName){

        $this->serverDescr = Builder::getGlobalServerDescription($serverName);
        $this->dbi = new DBInterface(getSQLCredentials($this->serverDescr), $serverName);

        $this->specialize($serverName);
        $this->say($serverName . " server initialized !");
    }

    public function specialize($serverName){

        $this->serverName = $serverName;
        $this->buildQueryFactory();
    }

    /**
     * Building the queryFactory based on namespaces and naming conventions.
     */
    private function buildQueryFactory(){

        try {
            $server = new \ReflectionClass($this->serverDescr["global_name"]);
            $namespace = $server->getNamespaceName();

            $queryFactoryName = $namespace."\\".$this->serverName."QueryFactory";
            $this->queryFactory = new $queryFactoryName($this->dbi);
        }
        catch (\ReflectionException $re){
            $this->say($re->getMessage());
            die();
        }

        $this->say($this->serverName."QueryFactory built successfully !");
    }


    /**
     * Utility debug function
     *
     * @param $something
     */
    public function say($something){

        if (!empty($this->serverName)){
            $date = new \DateTime();
            echo "\n$ ".$date->format(DATE_COOKIE)." > ".$this->serverName." Server > ".$something;
        }
    }
}