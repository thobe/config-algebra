package org.thobe.config.impl;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.thobe.config.Parameter;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.thobe.config.impl.NumericSetting.readFloatingPoint;
import static org.thobe.config.impl.NumericSetting.readInteger;
import static org.thobe.config.impl.Setting.booleanSetting;
import static org.thobe.config.impl.Setting.listSetting;
import static org.thobe.config.impl.Setting.setting;
import static org.thobe.config.impl.Setting.stringSetting;

public class ConfigurationTest
{
    // Simple settings
    static Setting<Boolean> boolean_parameter = booleanSetting( "boolean_parameter" );
    static Setting<Boolean> boolean_parameter_false = booleanSetting( "boolean_parameter_false", false );
    static Setting<Boolean> boolean_parameter_true = booleanSetting( "boolean_parameter_false", true );
    static Setting<String> string_parameter = stringSetting( "string_parameter" );
    static Setting<String> string_parameter_with_default = stringSetting( "string_parameter_with_default",
                                                                          "default value" );
    // Numeric settings
    static Setting<Byte> byte_parameter = setting( "byte_parameter", readInteger().asByte() );
    static Setting<Byte> byte_parameter_with_default = setting( "byte_parameter_with_default",
                                                                readInteger().asByte(), (byte) 42 );
    static Setting<Short> short_parameter = setting( "short_parameter", readInteger().asShort() );
    static Setting<Short> short_parameter_with_default = setting( "short_parameter_with_default",
                                                                  readInteger().asShort(), (short) 42 );
    static Setting<Integer> int_parameter = setting( "int_parameter", readInteger().asInteger() );
    static Setting<Integer> int_parameter_with_default = setting( "int_parameter_with_default",
                                                                  readInteger().asInteger(), 42 );
    static Setting<Long> long_parameter = setting( "long_parameter", readInteger().asLong() );
    static Setting<Long> long_parameter_with_default = setting( "long_parameter_with_default",
                                                                readInteger().asLong(), 42l );
    static Setting<Float> float_parameter = setting( "float_parameter", readFloatingPoint().asFloat() );
    static Setting<Float> float_parameter_with_default = setting( "float_parameter_with_default",
                                                                  readFloatingPoint().asFloat(), 3.14f );
    static Setting<Double> double_parameter = setting( "double_parameter", readFloatingPoint().asDouble() );
    static Setting<Double> double_parameter_with_default = setting( "double_parameter_with_default",
                                                                    readFloatingPoint().asDouble(), 3.14 );
    // List settings
    static Setting<List<String>> string_list_parameter = listSetting(
            "string_list_parameter", Conversion.NoConversion.<String>noConversion() );
    static Setting<List<String>> string_list_parameter_with_default_list = listSetting(
            "string_list_parameter_with_default_list", Conversion.NoConversion.<String>noConversion(),
            asList( "one", "two", "three" ) );
    static Setting<List<String>> string_list_parameter_with_default_values = listSetting(
            "string_list_parameter_with_default_values", Conversion.NoConversion.<String>noConversion(),
            "one", "two", "three" );
    static Setting<List<Integer>> plus_separated_list_parameter = listSetting(
            "plus_separated_list_parameter", "\\+", readInteger().asInteger() );
    static Setting<List<Integer>> plus_separated_list_parameter_with_default_list = listSetting(
            "plus_separated_list_parameter_with_default_list", "\\+", readInteger().asInteger(), asList( 1, 2, 3 ) );
    static Setting<List<Integer>> plus_separated_list_parameter_with_default_values = listSetting(
            "plus_separated_list_parameter_with_default_values", "\\+", readInteger().asInteger(), 1, 2, 3 );

    @Test
    public void shouldReadParameter() throws Exception
    {
        assertEquals( "hello world", configuration( "string_parameter", "hello world" ).get( string_parameter ) );
    }

    @Test
    public void shouldUseDefaultIfNotConfigured() throws Exception
    {
        assertEquals( "default value", new Configuration().get( string_parameter_with_default ) );
    }

    @Test
    public void shouldNotUseDefaultIfConfigured() throws Exception
    {
        assertEquals( "hello world", configuration( "string_parameter_with_default", "hello world" ).get(
                string_parameter_with_default ) );
    }

    @Test
    public void shouldThrowExceptionIfNotConfiguredAndWithoutDefault() throws Exception
    {
        // given
        Configuration configuration = new Configuration();

        // when
        try
        {
            configuration.get( string_parameter );
            fail( "should have thrown exception" );
        }
        // then
        catch ( SettingNotConfiguredException expected )
        {
            assertEquals( "The required setting [string_parameter] has not been configured.", expected.getMessage() );
        }
    }

    @Test
    public void shouldParseBooleanParameter() throws Exception
    {
        assertTrue( configuration( "boolean_parameter", "true" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "on" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "yes" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "enable" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "enabled" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "True" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "TRUE" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "On" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "ON" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "YES" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "Yes" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "Enable" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "ENABLE" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "Enabled" ).get( boolean_parameter ) );
        assertTrue( configuration( "boolean_parameter", "ENABLED" ).get( boolean_parameter ) );

        assertFalse( configuration( "boolean_parameter", "false" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "off" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "no" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "disable" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "disabled" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "False" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "FALSE" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "Off" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "OFF" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "No" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "NO" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "Disable" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "DISABLE" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "Disabled" ).get( boolean_parameter ) );
        assertFalse( configuration( "boolean_parameter", "DISABLED" ).get( boolean_parameter ) );

        try
        {
            configuration( "boolean_parameter", "junk" ).get( boolean_parameter );
            fail( "should have thrown exception" );
        }
        catch ( SettingNotConfiguredException expected )
        {
            assertThat( expected.getMessage(), startsWith(
                    "The setting [boolean_parameter] has been configured with an invalid value [junk]. " +
                    "[junk] is not a valid boolean value, valid values are [true, yes, on, enable, enabled] " +
                    "or [false, no, off, disable, disabled]. Configuration was done through the use of the " +
                    "foreign parameter [Mock for Parameter, hashCode: " ) );
        }

        try
        {
            configuration( "boolean_parameter", "junk" ).get( boolean_parameter );
            fail( "should have thrown exception" );
        }
        catch ( SettingNotConfiguredException expected )
        {
            assertThat( expected.getMessage(), startsWith(
                    "The setting [boolean_parameter] has been configured with an invalid value [junk]. " +
                    "[junk] is not a valid boolean value, valid values are [true, yes, on, enable, enabled] " +
                    "or [false, no, off, disable, disabled]. Configuration was done through the use of the " +
                    "foreign parameter [Mock for Parameter, hashCode: " ) );
        }

        try
        {
            configuration( "boolean_parameter", "junk" ).get( boolean_parameter );
            fail( "should have thrown exception" );
        }
        catch ( SettingNotConfiguredException expected )
        {
            assertThat( expected.getMessage(), startsWith(
                    "The setting [boolean_parameter] has been configured with an invalid value [junk]. " +
                    "[junk] is not a valid boolean value, valid values are [true, yes, on, enable, enabled] " +
                    "or [false, no, off, disable, disabled]. Configuration was done through the use of the " +
                    "foreign parameter [Mock for Parameter, hashCode: " ) );
        }
    }

    @Test
    public void shouldApplyDefaultValuesForBooleanParameters() throws Exception
    {
        // given
        Configuration configuration = new Configuration();

        // when
        try
        {
            configuration.get( boolean_parameter );
            fail( "should have thrown exception" );
        }
        // then
        catch ( SettingNotConfiguredException expected )
        {
            assertEquals( "The required setting [boolean_parameter] has not been configured.", expected.getMessage() );
        }

        assertTrue( configuration.get( boolean_parameter_true ) );
        assertFalse( configuration.get( boolean_parameter_false ) );
    }

    private static Configuration configuration( String name, String value )
    {
        Configuration configuration = new Configuration();
        Parameter parameter = mock( Parameter.class );
        when( parameter.name() ).thenReturn( name );
        configuration.configure( parameter, value );
        return configuration;
    }

    private static Matcher<String> startsWith( final String prefix )
    {
        return new TypeSafeMatcher<String>()
        {
            @Override
            public boolean matchesSafely( String item )
            {
                return item.startsWith( prefix );
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText( "String starting with " ).appendValue( prefix );
            }
        };
    }
}
