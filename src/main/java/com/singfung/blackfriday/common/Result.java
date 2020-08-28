package com.singfung.blackfriday.common;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Result<T>
{
    private Boolean status;
    private String message;
    private T data;

    public static <T> Result<T> success(String message) {
        return success(message, null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<T>(true, message, data);
    }

    public static <T> Result<T> failure(String message) {
        return new Result<T>(false, message, null);
    }

    public static <T> Result<T> failure(String message, T data) {
        return new Result<T>(false, message, data);
    }
}