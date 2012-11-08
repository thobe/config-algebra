package org.thobe.config.impl;

import org.thobe.config.InvalidConfigurationValueException;

public class InvalidRangeException extends InvalidConfigurationValueException
{
    private final Number value;
    private final Number lowerLimit;
    private final Number upperLimit;

    public InvalidRangeException( Number value, Number lowerLimit, Number upperLimit )
    {
        this( "%s is not within the valid range [%s,%s].", value,
              lowerLimit == null ? Double.NEGATIVE_INFINITY : lowerLimit,
              upperLimit == null ? Double.POSITIVE_INFINITY : upperLimit );
    }

    private InvalidRangeException( String format, Number value, Number lowerLimit, Number upperLimit )
    {
        super( format, value, lowerLimit, upperLimit );
        this.value = value;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    @Override
    public String value()
    {
        return value.toString();
    }
}
