package dev.hausfix.util;

import java.util.Objects;

public class Hash {

    public static String hashPassword(String password) {
        return Integer.toHexString(Objects.hash(password));
    }

}
