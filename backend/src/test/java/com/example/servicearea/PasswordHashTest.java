package com.example.servicearea;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    @Test
    void printHashFor123456() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        System.out.println("HASH=" + hash);
        String old = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iCtKBUy";
        System.out.println("MATCH_OLD=" + encoder.matches("123456", old));
        String dataSql = "$2a$10$IPop.7qBsmUGt1VQcmsVen4VHgnfFZfAWONJmjiijUH23qJAH27m";
        System.out.println("MATCH_DATA=" + encoder.matches("123456", dataSql));
    }
}
