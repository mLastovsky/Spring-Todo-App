package com.mlastovsky.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringTodoAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringTodoAppApplication.class, args);
    }

}
