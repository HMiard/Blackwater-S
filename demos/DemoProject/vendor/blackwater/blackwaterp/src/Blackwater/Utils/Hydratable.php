<?php

namespace Blackwater\Utils;

/**
 * Class Hydratable
 * @package Blackwater\Utils
 */
abstract class Hydratable {

    /**
     * Hydrating a class from an array.
     *
     * @param array $parameters
     */
    public function hydrate(array $parameters){

        foreach ($parameters as $key => $value){
            $method = 'set'.str_replace(' ', '', ucwords(str_replace('_', ' ', $key)));
            if (is_callable(array($this, $method)))
                $this->$method($value);
        }
    }
}