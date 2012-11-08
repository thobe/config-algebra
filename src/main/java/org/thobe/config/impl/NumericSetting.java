package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;

import static org.thobe.config.impl.NumericSetting.Comparison.*;

public abstract class NumericSetting
{
    public static NumericSetting readInteger()
    {
        return new LongSetting();
    }

    public static NumericSetting readFloatingPoint()
    {
        return new DoubleSetting();
    }

    public NumericSetting lowerLimit( long lowerLimit )
    {
        this.lowerLimit = lowerLimit;
        return this;
    }

    public NumericSetting upperLimit( long upperLimit )
    {
        this.upperLimit = upperLimit;
        return this;
    }

    public NumericSetting lowerLimit( double lowerLimit )
    {
        this.lowerLimit = lowerLimit;
        return this;
    }

    public NumericSetting upperLimit( double upperLimit )
    {
        this.upperLimit = upperLimit;
        return this;
    }

    public Conversion<String, Byte> asByte()
    {
        return conversion( BYTE_VALUE );
    }

    public Conversion<String, Short> asShort()
    {
        return conversion( SHORT_VALUE );
    }

    public Conversion<String, Integer> asInteger()
    {
        return conversion( INT_VALUE );
    }

    public Conversion<String, Long> asLong()
    {
        return conversion( LONG_VALUE );
    }

    public Conversion<String, Float> asFloat()
    {
        return conversion( FLOAT_VALUE );
    }

    public Conversion<String, Double> asDouble()
    {
        return conversion( DOUBLE_VALUE );
    }

    private Number lowerLimit, upperLimit;

    private NumericSetting()
    {
        // limited subclasses
    }

    private <T> Conversion<String, T> conversion( Conversion<Number, T> conversion )
    {
        return Conversion.Combined.conversion( reader(), conversion );
    }

    abstract Conversion<String, ? extends Number> reader();

    <N extends Number> Conversion<String, N> limited( Conversion<String, N> reader )
    {
        if ( lowerLimit != null && upperLimit != null )
        {
            return new DualLimit<N>( lowerLimit, upperLimit, reader );
        }
        else if ( lowerLimit != null )
        {
            return new LowerLimit<N>( lowerLimit, reader );
        }
        else if ( upperLimit != null )
        {
            return new UpperLimit<N>( upperLimit, reader );
        }
        else
        {
            return reader;
        }
    }

    private static class LongSetting extends NumericSetting
    {
        @Override
        Conversion<String, Long> reader()
        {
            return limited( READER );
        }

        @Override
        public Conversion<String, Long> asLong()
        {
            return reader();
        }

        private static Conversion<String, Long> READER = new Conversion<String, Long>()
        {
            @Override
            public Long convert( String value ) throws InvalidConfigurationValueException
            {
                value = value.trim();
                int radix = 10;
                if ( value.charAt( 0 ) == '0' )
                {
                    switch ( value.charAt( 1 ) )
                    {
                    case 'x':
                    case 'X':
                        radix = 16;
                        value = value.substring( 2 );
                        break;
                    case 'o':
                    case 'O':
                        radix = 8;
                        value = value.substring( 2 );
                        break;
                    case 'b':
                    case 'B':
                        radix = 2;
                        value = value.substring( 2 );
                        break;
                    }
                }
                try
                {
                    return Long.parseLong( value, radix );
                }
                catch ( NumberFormatException exception )
                {
                    throw new InvalidNumberException( value, exception );
                }
            }
        };
    }

    private static class DoubleSetting extends NumericSetting
    {
        @Override
        Conversion<String, Double> reader()
        {
            return limited( READER );
        }

        @Override
        public Conversion<String, Double> asDouble()
        {
            return reader();
        }

        private static Conversion<String, Double> READER = new Conversion<String, Double>()
        {
            @Override
            public Double convert( String value ) throws InvalidConfigurationValueException
            {
                return Double.parseDouble( value.trim() );
            }
        };
    }

    private static <T extends Number> T verified( Number value, T tentative ) throws InvalidRangeException
    {
        if ( value instanceof Double )
        {
            if ( value.doubleValue() != tentative.doubleValue() )
            {
                throw invalidRange( value, tentative.getClass() );
            }
        }
        else if ( value instanceof Long )
        {
            if ( value.longValue() != tentative.longValue() )
            {
                throw invalidRange( value, tentative.getClass() );
            }
        }
        return tentative;
    }

    private static InvalidRangeException invalidRange( Number value, Class<? extends Number> numberType )
    {
        Number min_value, max_value;
        try
        {
            min_value = (Number) numberType.getField( "MIN_VALUE" ).get( null );
        }
        catch ( Exception e )
        {
            min_value = null;
        }
        try
        {
            max_value = (Number) numberType.getField( "MAX_VALUE" ).get( null );
        }
        catch ( Exception e )
        {
            max_value = null;
        }
        return new InvalidRangeException( value, min_value, max_value );
    }

    private static final Conversion<Number, Byte> BYTE_VALUE = new Conversion<Number, Byte>()
    {
        @Override
        public Byte convert( Number value ) throws InvalidConfigurationValueException
        {
            return verified( value, value.byteValue() );
        }
    };

    private static final Conversion<Number, Short> SHORT_VALUE = new Conversion<Number, Short>()
    {
        @Override
        public Short convert( Number value ) throws InvalidConfigurationValueException
        {
            return verified( value, value.shortValue() );
        }
    };

    private static final Conversion<Number, Integer> INT_VALUE = new Conversion<Number, Integer>()
    {
        @Override
        public Integer convert( Number value ) throws InvalidConfigurationValueException
        {
            return verified( value, value.intValue() );
        }
    };

    private static final Conversion<Number, Long> LONG_VALUE = new Conversion<Number, Long>()
    {
        @Override
        public Long convert( Number value ) throws InvalidConfigurationValueException
        {
            long result = value.longValue();
            if ( value instanceof Double || value instanceof Float )
            {
                double floatingPoint = value.doubleValue();
                if ( floatingPoint < Long.MIN_VALUE || floatingPoint > Long.MAX_VALUE )
                {
                    throw new InvalidRangeException( value, Long.MIN_VALUE, Long.MAX_VALUE );
                }
            }
            return result;
        }
    };

    private static final Conversion<Number, Float> FLOAT_VALUE = new Conversion<Number, Float>()
    {
        @Override
        public Float convert( Number value ) throws InvalidConfigurationValueException
        {
            return value.floatValue();
        }
    };

    private static final Conversion<Number, Double> DOUBLE_VALUE = new Conversion<Number, Double>()
    {
        @Override
        public Double convert( Number value ) throws InvalidConfigurationValueException
        {
            return value.doubleValue();
        }
    };

    private static class DualLimit<N extends Number> implements Conversion<String, N>
    {
        private final Number lowerLimit;
        private final Number upperLimit;
        private final Conversion<String, N> reader;

        public DualLimit( Number lowerLimit, Number upperLimit, Conversion<String, N> reader )
        {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
            this.reader = reader;
        }

        @Override
        public N convert( String value ) throws InvalidConfigurationValueException
        {
            N number = reader.convert( value );
            if ( verify( number, GREATER_THAN, lowerLimit ) && verify( number, LESS_THAN, upperLimit ) )
            {
                return number;
            }
            else
            {
                throw new InvalidRangeException( number, lowerLimit, upperLimit );
            }
        }
    }

    private static class UpperLimit<N extends Number> implements Conversion<String, N>
    {
        private final Number upperLimit;
        private final Conversion<String, N> reader;

        public UpperLimit( Number upperLimit, Conversion<String, N> reader )
        {
            this.upperLimit = upperLimit;
            this.reader = reader;
        }

        @Override
        public N convert( String value ) throws InvalidConfigurationValueException
        {
            N number = reader.convert( value );
            if ( verify( number, LESS_THAN, upperLimit ) )
            {
                return number;
            }
            else
            {
                throw new InvalidRangeException( number, null, upperLimit );
            }
        }
    }

    private static class LowerLimit<N extends Number> implements Conversion<String, N>
    {
        private final Number lowerLimit;
        private final Conversion<String, N> reader;

        public LowerLimit( Number lowerLimit, Conversion<String, N> reader )
        {
            this.lowerLimit = lowerLimit;
            this.reader = reader;
        }

        @Override
        public N convert( String value ) throws InvalidConfigurationValueException
        {
            N number = reader.convert( value );
            if ( verify( number, GREATER_THAN, lowerLimit ) )
            {
                return number;
            }
            else
            {
                throw new InvalidRangeException( number, lowerLimit, null );
            }
        }
    }

    private static boolean verify( Number lhs, Comparison comparison, Number rhs )
    {
        if ( lhs instanceof Double || lhs instanceof Float )
        {
            if ( rhs instanceof Double || rhs instanceof Float )
            {
                return comparison.compare( lhs.doubleValue(), rhs.doubleValue() );
            }
            else
            {
                return comparison.compare( lhs.doubleValue(), rhs.longValue() );
            }
        }
        else
        {
            if ( rhs instanceof Double || rhs instanceof Float )
            {
                return comparison.compare( lhs.longValue(), rhs.doubleValue() );
            }
            else
            {
                return comparison.compare( lhs.longValue(), rhs.longValue() );
            }
        }
    }

    enum Comparison
    {
        LESS_THAN
                {
                    @Override
                    boolean compare( double lhs, double rhs )
                    {
                        return lhs < rhs;
                    }

                    @Override
                    boolean compare( double lhs, long rhs )
                    {
                        return lhs < rhs;
                    }

                    @Override
                    boolean compare( long lhs, double rhs )
                    {
                        return lhs < rhs;
                    }

                    @Override
                    boolean compare( long lhs, long rhs )
                    {
                        return lhs < rhs;
                    }
                },
        GREATER_THAN
                {
                    @Override
                    boolean compare( double lhs, double rhs )
                    {
                        return lhs > rhs;
                    }

                    @Override
                    boolean compare( double lhs, long rhs )
                    {
                        return lhs > rhs;
                    }

                    @Override
                    boolean compare( long lhs, double rhs )
                    {
                        return lhs > rhs;
                    }

                    @Override
                    boolean compare( long lhs, long rhs )
                    {
                        return lhs > rhs;
                    }
                };

        abstract boolean compare( double lhs, double rhs );

        abstract boolean compare( double lhs, long rhs );

        abstract boolean compare( long lhs, double rhs );

        abstract boolean compare( long lhs, long rhs );
    }
}
