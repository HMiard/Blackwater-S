<?php

namespace Blackwater;

use Ratchet\Server\IoServer;
use Ratchet\Http\HttpServer;
use Ratchet\WebSocket\WsServer;

class Builder
{
    /**
     * Builds and run a Blackwater server according to its name
     * on the specified port.
     *
     * @param $serverName
     * @param $port
     */
    static function buildAndRun($serverName, $port = 8080){

        $serverName = strtolower($serverName);
        $serverName = ucfirst($serverName);

        $className = $serverName."Server\\".$serverName."Server";
        $build = new $className($serverName);

        file_put_contents(
            "bin/watcher",
            getmypid()
        );

        $server = IoServer::factory(
            new HttpServer(
                new WsServer(
                    $build
                )
            ),
            $port
        );
        echo "\nBlackwater > ".$serverName." server running on localhost, port ".$port." !\n";
        $server->run();
    }

    /**
     * Returning some useful constants.
     *
     * @param $serverName
     * @return array
     */
    static function getGlobalServerDescription($serverName){

        $global_name = $serverName."Server\\".$serverName."Server";
        $r = new \ReflectionClass($global_name);
        $dir = dirname($r->getFileName());

        return array(
            "name" => $serverName,
            "global_name" => $global_name,
            "src_dir" => $dir,
            "conf_dir" => $dir."/conf",
            "bin_dir" => $dir."/bin"
        );
    }
}
