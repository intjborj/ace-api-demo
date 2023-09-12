package com.hisd3.hismk2.utils

class Formatter {
    static UUID toUUID(Object field) {
        return UUID.fromString(field.toString())
    }
}
