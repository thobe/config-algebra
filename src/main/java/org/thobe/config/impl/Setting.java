package org.thobe.config.impl;

import java.util.List;
import java.util.regex.Pattern;

import org.thobe.config.InvalidConfigurationValueException;
import org.thobe.config.Parameter;

import static java.util.Arrays.asList;
import static org.thobe.config.impl.DefaultValue.listOf;

public final class Setting<T> implements Parameter
{
    public static <T> Setting<T> setting( String name, Conversion<String, T> conversion )
    {
        return new Setting<T>( name, conversion, DefaultValue.<T>noDefaultValue() );
    }

    public static <T> Setting<T> setting( String name, Conversion<String, T> conversion, T defaultValue )
    {
        return new Setting<T>( name, conversion, DefaultValue.<T>defaultValue( defaultValue ) );
    }

    public static Setting<Boolean> booleanSetting( String name )
    {
        return new Setting<Boolean>( name, BOOLEAN, DefaultValue.<Boolean>noDefaultValue() );
    }

    public static Setting<Boolean> booleanSetting( String name, boolean defaultValue )
    {
        return new Setting<Boolean>( name, BOOLEAN, DefaultValue.<Boolean>defaultValue( defaultValue ) );
    }

    public static Setting<String> stringSetting( String name )
    {
        return setting( name, Conversion.NoConversion.<String>noConversion() );
    }

    public static Setting<String> stringSetting( String name, String defaultValue )
    {
        return setting( name, Conversion.NoConversion.<String>noConversion(), defaultValue );
    }

    public static <T> Setting<List<T>> listSetting( String name, Conversion<String, T> conversion )
    {
        return setting( name, new ListConversion<T>( ListConversion.COMMA_SEPARATED, conversion ) );
    }

    public static <T> Setting<List<T>> listSetting( String name, String separator, Conversion<String, T> conversion )
    {
        return setting( name, new ListConversion<T>( Pattern.compile( separator ), conversion ) );
    }

    public static <T> Setting<List<T>> listSetting( String name, Conversion<String, T> conversion,
                                                    T firstDefault, T... moreDefaults )
    {
        return new Setting<List<T>>( name, new ListConversion<T>( ListConversion.COMMA_SEPARATED, conversion ),
                                     DefaultValue.<T>defaultList( firstDefault, moreDefaults ) );
    }

    public static <T> Setting<List<T>> listSetting( String name, String separator, Conversion<String, T> conversion,
                                                    T firstDefault, T... moreDefaults )
    {
        return new Setting<List<T>>( name, new ListConversion<T>( Pattern.compile( separator ), conversion ),
                                     DefaultValue.<T>defaultList( firstDefault, moreDefaults ) );
    }

    public static <T> Setting<List<T>> listSetting( String name, Conversion<String, T> conversion,
                                                    List<T> defaultValue )
    {
        return setting( name, new ListConversion<T>( ListConversion.COMMA_SEPARATED, conversion ), defaultValue );
    }

    public static <T> Setting<List<T>> listSetting( String name, String separator, Conversion<String, T> conversion,
                                                    List<T> defaultValue )
    {
        return setting( name, new ListConversion<T>( Pattern.compile( separator ), conversion ), defaultValue );
    }

    private final String name;
    private final Conversion<String, T> conversion;
    private final DefaultValue<T> defaultValue;

    private Setting( String name, Conversion<String, T> conversion, DefaultValue<T> defaultValue )
    {
        this.name = name;
        this.conversion = conversion;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString()
    {
        StringBuilder repr = new StringBuilder( "Setting{" ).append( name );
        defaultValue.formatTo( repr );
        return repr.append( '}' ).toString();
    }

    @Override
    public String name()
    {
        return name;
    }

    @Override
    public void verify( String value ) throws InvalidConfigurationValueException
    {
        parse( value );
    }

    T parse( String value ) throws InvalidConfigurationValueException
    {
        try
        {
            if ( value == null )
            {
                return defaultValue.get( this );
            }
            try
            {
                return conversion.convert( value );
            }
            catch ( UseDefault VALUE )
            {
                return defaultValue.get( this );
            }
        }
        catch ( NoConfigurationException e )
        {
            throw new NoConfigurationValueException( e );
        }
    }

    private static String[]
            TRUE_VALUES = {"true", "yes", "on", "enable", "enabled"},
            FALSE_VALUES = {"false", "no", "off", "disable", "disabled"};

    private static final Conversion<String, Boolean> BOOLEAN = new Conversion<String, Boolean>()
    {
        @Override
        public Boolean convert( String value ) throws InvalidConfigurationValueException
        {
            value = value.trim();
            for ( String trueValue : TRUE_VALUES )
            {
                if ( trueValue.equalsIgnoreCase( value ) )
                {
                    return true;
                }
            }
            for ( String falseValue : FALSE_VALUES )
            {
                if ( falseValue.equalsIgnoreCase( value ) )
                {
                    return false;
                }
            }
            throw new InvalidBooleanValueException( value );
        }
    };

    T getDefaultValue() throws NoConfigurationValueException
    {
        try
        {
            return defaultValue.get( this );
        }
        catch ( NoConfigurationException e )
        {
            throw new NoConfigurationValueException( e );
        }
    }

    private static class ListConversion<T> implements Conversion<String, List<T>>
    {
        static final Pattern COMMA_SEPARATED = Pattern.compile( "," );
        private final Pattern separator;
        private final Conversion<String, T> partConversion;

        ListConversion( Pattern separator, Conversion<String, T> partConversion )
        {
            this.separator = separator;
            this.partConversion = partConversion;
        }

        @Override
        public List<T> convert( String value ) throws InvalidConfigurationValueException
        {
            value = value.trim();
            if ( value.length() == 0 )
            {
                throw UseDefault.VALUE;
            }
            String[] parts = separator.split( value );
            @SuppressWarnings("unchecked")
            T[] result = (T[]) new Object[parts.length];
            for ( int i = 0; i < parts.length; i++ )
            {
                result[i] = partConversion.convert( value );
            }
            return listOf( result );
        }
    }

    private static class UseDefault extends Error
    {
        public static final UseDefault VALUE = new UseDefault();

        @Override
        public synchronized Throwable fillInStackTrace()
        {
            return this;
        }
    }

    private static class InvalidBooleanValueException extends InvalidConfigurationValueException
    {
        private final String value;

        InvalidBooleanValueException( String value )
        {
            super( "[%s] is not a valid boolean value, valid values are %s or %s.",
                   value, asList( TRUE_VALUES ), asList( FALSE_VALUES ) );
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    T unsafeCast( Object value )
    {
        return (T) value;
    }
}
