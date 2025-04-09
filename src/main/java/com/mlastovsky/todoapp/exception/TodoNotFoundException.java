package com.mlastovsky.todoapp.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TodoNotFoundException extends RuntimeException {

    private final String msg;

}
