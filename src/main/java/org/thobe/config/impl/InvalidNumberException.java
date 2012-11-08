package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;

class InvalidNumberException extends InvalidConfigurationValueException
{
    private final String value;

    InvalidNumberException( String value, NumberFormatException cause )
    {
        super( cause, "[%s] is not a valid numerical string.", value );
        this.value = value;
    }

    @Override
    public String value()
    {
        return value;
    }
}
