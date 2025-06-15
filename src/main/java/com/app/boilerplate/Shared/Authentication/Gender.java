package com.app.boilerplate.Shared.Authentication;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    Male(0), Female(1);

    private final int code;

    Gender(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static Gender fromCode(int code) {
        for (Gender g : values()) {
            if (g.code == code) return g;
        }
        throw new IllegalArgumentException("Invalid gender code: " + code);
    }
}
