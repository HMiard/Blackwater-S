<?php

    /**
    * @param $desc array
    * @return array
    */
    function getSQLCredentials($desc){

        $creds = @fopen($desc["conf_dir"]."/credentials", "r");
        $ret = array();

        if ($creds === false){
            echo "\nBlackwater > No custom database credentials found in ".$desc['conf_dir'].".";
            echo "\nBlackwater > The server will use the default credentials.\n";

            $creds = fopen(__DIR__."/credentials", "r");
        }
        if ($creds !== false)
            while (($buffer = fgets($creds)) !== false){
                $t = explode(":", $buffer);
                $ret[$t[0]] = str_replace(PHP_EOL, '', $t[1]);
            }

        return $ret;
    }