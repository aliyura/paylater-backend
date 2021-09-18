package com.syrol.paylater.enums;

public enum RecipientType {

    TO,
    CC,
    BCC;

    public String value() {
        return name();
    }

    public static RecipientType fromValue(String v) {
        return valueOf(v);
    }

}