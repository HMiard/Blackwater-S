<?php

namespace Blackwater\Utils;

/**
 * Class Exportable
 * @package Blackwater\Utils
 */
abstract class Exportable {

    /**
     * Exporting attributes from a class as an array.
     *
     * @return array
     */
    public function export(){
        $ret = array();
        foreach(get_object_vars($this) as $key => $value){
            $method = 'get'.str_replace(' ', '', ucwords(str_replace('_', ' ', $key)));
            if (is_callable(array($this, $method)))
                $ret[$key] = $this->$method();
        }
        return $ret;
    }
}