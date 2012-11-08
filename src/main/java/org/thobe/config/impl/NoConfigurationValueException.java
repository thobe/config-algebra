package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;

class NoConfigurationValueException extends InvalidConfigurationValueException
{
    NoConfigurationValueException( NoConfigurationException noConfig )
    {
        super( noConfig, "No configuration value supplied for configuration parameter [%s].",
               noConfig.settingsParameter().name() );
    }

    @Override
    public String value()
    {
        return null;
    }
}
