package org.thobe.config;

import java.util.Locale;

public abstract class InvalidConfigurationValueException extends Exception
{
    public InvalidConfigurationValueException( String message, Object... parameters )
    {
        super( String.format( message, parameters ) );
    }

    public InvalidConfigurationValueException( Throwable cause, String message, Object... parameters )
    {
        super( String.format( message, parameters ), cause );
    }

    public String localizeMessage( Locale locale )
    {
        return getMessage();
    }

    public abstract String value();
}
