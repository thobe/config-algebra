package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;
import org.thobe.config.Parameter;

public class SettingNotConfiguredException extends Exception
{
    private final Setting<?> setting;

    public SettingNotConfiguredException( Setting<?> setting, NoConfigurationValueException invalid )
    {
        super( String.format( "The required setting [%s] has not been configured.", setting.name() ), invalid );
        this.setting = setting;
    }

    SettingNotConfiguredException( Setting<?> setting, InvalidConfigurationValueException invalid, Parameter parameter )
    {
        super( String.format( "The setting [%s] has been configured with an invalid value [%s]. %s " +
                              "Configuration was done through the use of the foreign parameter [%s].",
                              setting.name(), invalid.value(), invalid.getMessage(), parameter ), invalid );
        this.setting = setting;
    }
}
