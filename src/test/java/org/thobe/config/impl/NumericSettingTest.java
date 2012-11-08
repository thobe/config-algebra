package org.thobe.config.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class NumericSettingTest
{
    @Test
    public void shouldReadNumericalStringAsLong() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readInteger().asLong();

        // when
        long value = reader.convert( "42" );

        // then
        assertEquals( 42, value );
    }

    @Test
    public void shouldReadHexadecimalStringAsLong() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readInteger().asLong();

        // when
        long value = reader.convert( "0x2A" );

        // then
        assertEquals( 42, value );
    }

    @Test
    public void shouldReadOctalStringAsLong() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readInteger().asLong();

        // when
        long value = reader.convert( "0o52" );

        // then
        assertEquals( 42, value );
    }

    @Test
    public void shouldReadBinaryStringAsLong() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readInteger().asLong();

        // when
        long value = reader.convert( "0b101010" );

        // then
        assertEquals( 42l, value );
    }

    @Test
    public void shouldThrowExceptionForByteNotInByteRange() throws Exception
    {
        // given
        Conversion<String, Byte> reader = NumericSetting.readInteger().asByte();

        // when
        try
        {
            reader.convert( "0xFF" );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidRangeException expected )
        {
            assertEquals( "255 is not within the valid range [-128,127].", expected.getMessage() );
        }
    }

    @Test
    public void shouldThrowExceptionForShortNotInShortRange() throws Exception
    {
        // given
        Conversion<String, Short> reader = NumericSetting.readInteger().asShort();

        // when
        try
        {
            reader.convert( "0xFFFF" );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidRangeException expected )
        {
            assertEquals( "65535 is not within the valid range [-32768,32767].", expected.getMessage() );
        }
    }

    @Test
    public void shouldThrowExceptionForIntNotInIntRange() throws Exception
    {
        // given
        Conversion<String, Integer> reader = NumericSetting.readInteger().asInteger();

        // when
        try
        {
            reader.convert( "0xFFFFFFFF" );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidRangeException expected )
        {
            assertEquals( "4294967295 is not within the valid range [-2147483648,2147483647].", expected.getMessage() );
        }
    }

    @Test
    public void shouldThrowExceptionForLongNotInLongRange() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readInteger().asLong();

        // when
        try
        {
            reader.convert( "0xFFFFFFFFFFFFFFFF" );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidNumberException expected )
        {
            assertEquals( "[FFFFFFFFFFFFFFFF] is not a valid numerical string.", expected.getMessage() );
        }
    }

    @Test
    public void shouldThrowExceptionForLongReadAsDoubleNotInLongRange() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readFloatingPoint().asLong();
        double number = Long.MAX_VALUE * 10.0;

        // when
        try
        {
            reader.convert( "" + number );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidRangeException expected )
        {
            assertEquals(
                    "9.223372036854776E19 is not within the valid range [-9223372036854775808,9223372036854775807].",
                    expected.getMessage() );
        }
    }

    @Test
    public void shouldRejectValueLargerThanUpperLimit() throws Exception
    {
        // given
        Conversion<String, Long> reader = NumericSetting.readInteger().upperLimit( 1000 ).asLong();

        // when
        try
        {
            reader.convert( "1001" );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidRangeException expected )
        {
            assertEquals( "1001 is not within the valid range [-9223372036854775808,1000].", expected.getMessage() );
        }
    }

    @Test
    public void shouldReportSpecifiedLimitsWhenRejectingNumberOutOfTypeRange() throws Exception
    {
        // given
        Conversion<String, Integer> reader = NumericSetting.readInteger().upperLimit( 1000 ).asInteger();

        // when
        try
        {
            reader.convert( "0xFFFFFFFF" );
            fail( "should have thrown exception" );
        }
        // then
        catch ( InvalidRangeException expected )
        {
            assertEquals( "4294967295 is not within the valid range [-2147483648,1000].", expected.getMessage() );
        }
    }
}
