package org.thobe.config.impl;

public class NoConfigurationException extends Exception
{
    private final Setting<?> setting;

    public NoConfigurationException( Setting<?> setting )
    {
        super( String.format( "No value specified for configuration parameter [%s],", setting.name() ) );
        this.setting = setting;
    }

    public Setting<?> settingsParameter()
    {
        return setting;
    }
}
