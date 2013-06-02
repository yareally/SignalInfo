package com.cc.signalinfo.signals;

import com.cc.signalinfo.enums.NetworkType;
import com.cc.signalinfo.enums.Signal;

/**
 * @author Wes Lanning
 * @version 2013-04-29
 */
public class SignalValue
{
    // TODO: decide sometime if I will ever need this or not
    // currently not used
    private Signal      name;
    private String      value;
    private NetworkType type;
    private int         resourceId;

    private SignalValue(Builder builder)
    {
        this.name = builder.name;
        this.value = builder.value;
        this.resourceId = builder.resourceId;
        this.type = builder.type;
    }

    public String getValueStr()
    {
        return value;
    }

    public int getValue()
    {
        return Integer.parseInt(value);
    }

    public Signal getName()
    {
        return name;
    }

    public NetworkType getType()
    {
        return type;
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public boolean enabled()
    {
        return !value.isEmpty();
    }

    public static class Builder
    {
        // required members
        private final Signal      name;
        private final NetworkType type;

        // optional members
        private String value = "";

        // the id for this signalValue in the activity/fragment to find it easily
        private int resourceId;

        public Builder(Signal name, NetworkType type)
        {
            this.name = name;
            this.type = type;
            resourceId = 0;
        }

        public Builder value(String value)
        {
            this.value = value;
            return this;
        }

        public Builder value(int value)
        {
            return value(String.valueOf(value));
        }

        public Builder id(int resourceId)
        {
            this.resourceId = resourceId;
            return this;
        }

        public SignalValue build(Builder builder)
        {
            return new SignalValue(this);
        }
    }
}
