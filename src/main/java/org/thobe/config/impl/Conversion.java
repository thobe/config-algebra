package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;

public interface Conversion<SOURCE, TARGET>
{
    TARGET convert( SOURCE value ) throws InvalidConfigurationValueException;

    class NoConversion<T> implements Conversion<T, T>
    {
        @SuppressWarnings("unchecked")
        public static <T> Conversion<T, T> noConversion()
        {
            return NONE;
        }

        private static final NoConversion NONE = new NoConversion();

        @Override
        public T convert( T value )
        {
            return value;
        }
    }

    public class Combined<SOURCE, INTERMEDIATE, TARGET> implements Conversion<SOURCE, TARGET>
    {
        public static <SOURCE, INTERMEDIATE, TARGET> Conversion<SOURCE, TARGET> conversion(
                Conversion<SOURCE, INTERMEDIATE> first, Conversion<? super INTERMEDIATE, TARGET> other )
        {
            return new Combined<SOURCE, INTERMEDIATE, TARGET>( first, other );
        }

        private final Conversion<SOURCE, INTERMEDIATE> first;
        private final Conversion<? super INTERMEDIATE, TARGET> other;

        private Combined( Conversion<SOURCE, INTERMEDIATE> first, Conversion<? super INTERMEDIATE, TARGET> other )
        {
            this.first = first;
            this.other = other;
        }

        @Override
        public TARGET convert( SOURCE value ) throws InvalidConfigurationValueException
        {
            return other.convert( first.convert( value ) );
        }
    }
}
