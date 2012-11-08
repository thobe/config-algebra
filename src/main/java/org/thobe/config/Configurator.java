package org.thobe.config;

public interface Configurator
{
    void configure( Parameter parameter, String value ) throws IllegalArgumentException;
}
