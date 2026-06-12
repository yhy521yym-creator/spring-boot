package com.example.servicearea;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后端启动类
 */
@SpringBootApplication
@MapperScan("com.example.servicearea.mapper")
public class ServiceAreaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAreaApplication.class, args);
    }
}


