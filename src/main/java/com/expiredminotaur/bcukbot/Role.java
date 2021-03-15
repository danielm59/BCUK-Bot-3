package com.expiredminotaur.bcukbot;

import java.util.HashMap;
import java.util.Map;

public enum Role
{
    USER(0),
    MOD(1),
    MANAGER(2),
    ADMIN(3);

    private final int value;
    private static final Map<Integer, Role> map = new HashMap<>();

    Role(int value)
    {
        this.value = value;
    }

    static
    {
        for (Role role : Role.values())
        {
            map.put(role.value, role);
        }
    }

    public static Role valueOf(int roleId)
    {
        return map.get(roleId);
    }

    public int getValue()
    {
        return value;
    }
}
