package org.thobe.config.impl;

import java.util.Locale;

public class ConflictingConfigurationException extends Exception
{
    private final Setting<?> configured;
    private final Setting<?> attempted;

    public ConflictingConfigurationException( Setting<?> configured, Setting<?> attempted )
    {
        super( String.format( "Attempted to configure [%s] by %s, but it is already configured by %s.",
                              configured.name(), attempted, configured ) );
        this.configured = configured;
        this.attempted = attempted;
    }

    public Setting<?> configuredSetting()
    {
        return configured;
    }

    public Setting<?> attemptedSetting()
    {
        return attempted;
    }

    public String localizedMessage( Locale locale )
    {
        return getMessage();
    }
}
