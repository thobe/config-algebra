package org.thobe.config;

public interface Parameter
{
    String name();

    void verify( String value ) throws InvalidConfigurationValueException;
}
