package org.thobe.config.impl;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

import org.thobe.config.Configurator;
import org.thobe.config.InvalidConfigurationValueException;
import org.thobe.config.Parameter;

public class Configuration implements Configurator
{
    private final Locale locale;
    private final Map<String, Setting<?>> settings = new HashMap<String, Setting<?>>();
    private final Map<Setting<?>, Object> verified = new IdentityHashMap<Setting<?>, Object>();
    private final Map<String, UninitializedParameter> uninitialized = new HashMap<String, UninitializedParameter>();

    public Configuration()
    {
        this( Locale.getDefault() );
    }

    public Configuration( Locale locale )
    {
        this.locale = locale;
    }

    public synchronized <T> T get( Setting<T> setting ) throws SettingNotConfiguredException
    {
        Object value = verified.get( setting );
        if ( value == null )
        {
            settings.put( setting.name(), setting );
            UninitializedParameter uninitializedParameter = uninitialized.remove( setting.name() );
            if ( uninitializedParameter != null )
            {
                try
                {
                    value = setting.parse( uninitializedParameter.value );
                }
                catch ( InvalidConfigurationValueException invalid )
                {
                    uninitialized.put( setting.name(), uninitializedParameter );
                    throw new SettingNotConfiguredWithValidValueException(
                            setting, invalid, uninitializedParameter.parameter );
                }
                verified.put( setting, value );
            }
        }
        if ( value == null )
        {
            try
            {
                value = setting.getDefaultValue();
            }
            catch ( NoConfigurationValueException e )
            {
                throw new SettingNotConfiguredException( setting, e );
            }
        }
        return setting.unsafeCast( value );
    }

    public synchronized <T> void set( Setting<T> setting, T value ) throws ConflictingConfigurationException
    {
        Setting<?> previous = settings.get( setting.name() );
        if ( previous == null )
        {
            settings.put( setting.name(), setting );
        }
        else if ( previous != setting )
        {
            throw new ConflictingConfigurationException( previous, setting );
        }
        verified.put( setting, value );
    }

    @Override
    public synchronized void configure( Parameter parameter, String value ) throws IllegalArgumentException
    {
        try
        {
            Setting<?> setting;
            if ( parameter instanceof Setting<?> )
            {
                setting = (Setting<?>) parameter;
            }
            else
            {
                setting = settings.get( parameter.name() );
            }
            if ( setting != null )
            {
                setFromString( setting, value );
            }
            else
            {
                parameter.verify( value );
                uninitialized.put( parameter.name(), new UninitializedParameter( parameter, value ) );
            }
        }
        catch ( InvalidConfigurationValueException invalid )
        {
            throw new IllegalArgumentException( invalid.localizeMessage( locale ), invalid );
        }
        catch ( ConflictingConfigurationException conflict )
        {
            throw new IllegalArgumentException( conflict.localizedMessage( locale ), conflict );
        }
    }

    private <T> void setFromString( Setting<T> setting, String value )
            throws InvalidConfigurationValueException, ConflictingConfigurationException
    {
        set( setting, setting.parse( value ) );
    }

    private static class UninitializedParameter
    {
        final Parameter parameter;
        final String value;

        UninitializedParameter( Parameter parameter, String value )
        {
            this.parameter = parameter;
            this.value = value;
        }
    }
}
