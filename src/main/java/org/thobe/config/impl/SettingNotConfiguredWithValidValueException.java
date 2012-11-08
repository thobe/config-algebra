package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;
import org.thobe.config.Parameter;

class SettingNotConfiguredWithValidValueException extends SettingNotConfiguredException
{
    SettingNotConfiguredWithValidValueException( Setting<?> setting, InvalidConfigurationValueException invalid,
                                                 Parameter parameter )
    {
        super( setting, invalid, parameter );
    }
}
