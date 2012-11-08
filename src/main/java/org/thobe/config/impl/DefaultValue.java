package org.thobe.config.impl;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.unmodifiableList;

abstract class DefaultValue<T>
{
    abstract T get( Setting<T> setting ) throws NoConfigurationException;

    static <T> DefaultValue<T> defaultValue( final T defaultValue )
    {
        return new DefaultValue<T>()
        {
            @Override
            T get( Setting<T> setting ) throws NoConfigurationException
            {
                return defaultValue;
            }

            @Override
            void formatTo( StringBuilder repr )
            {
                repr.append( " defaultValue=" ).append( defaultValue );
            }

            @Override
            public String toString()
            {
                return "<DefaultValue: " + defaultValue + '>';
            }
        };
    }

    static <T> DefaultValue<List<T>> defaultList( T firstValue, T[] moreValues )
    {
        @SuppressWarnings("unchecked")
        T[] result = (T[]) new Object[moreValues == null ? 1 : (moreValues.length + 1)];
        result[0] = firstValue;
        if ( moreValues != null )
        {
            System.arraycopy( moreValues, 0, result, 1, moreValues.length );
        }
        return defaultValue( listOf( result ) );
    }

    @SuppressWarnings("unchecked")
    static <T> DefaultValue<T> noDefaultValue()
    {
        return NONE;
    }

    abstract void formatTo( StringBuilder repr );

    private static final DefaultValue NONE = new DefaultValue()
    {
        @Override
        Object get( Setting setting ) throws NoConfigurationException
        {
            throw new NoConfigurationException( setting );
        }

        @Override
        void formatTo( StringBuilder repr )
        {
            // nothing
        }

        @Override
        public String toString()
        {
            return "<NoDefaultValue>";
        }
    };

    private DefaultValue()
    {
        // limited subclasses
    }

    static <T> List<T> listOf( T[] result )
    {
        return unmodifiableList( Arrays.asList( result ) );
    }
}
