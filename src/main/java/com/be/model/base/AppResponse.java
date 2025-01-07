package com.be.model.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppResponse<T> {
    private String path;
    private String error;
    private String message;
    private String details;
    @Builder.Default
    private String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    private int status;
    private T data;

    public static <T> AppResponse<T> buildResponse(String error, String path, String message, int statusCode, T data) {
        return AppResponse.<T>builder()
                .status(statusCode)
                .path(path)
                .error(error)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> AppResponse<T> buildResponse(HttpStatus httpStatus, T data) {
        return AppResponse.<T>builder()
                .status(httpStatus.value())
                .message(httpStatus.name())
                .data(data)
                .build();
    }

    public static <T> AppResponse<T> success(T data, String path) {
        return AppResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .path(path)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> AppResponse<T> error(String error, String path, HttpStatus status) {
        return AppResponse.<T>builder()
                .status(status.value())
                .path(path)
                .error(error)
                .message(status.getReasonPhrase())
                .build();
    }

    public AppResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }
}