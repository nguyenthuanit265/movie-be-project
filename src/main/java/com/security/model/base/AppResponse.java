package com.security.model.base;

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
public class AppResponse {
    private String path;
    private String error;
    private String message;
    private String details;
    private String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    private int status;
    private Object data;

    public static AppResponse buildResponse(String error, String path, String message, int statusCode, Object data) {
        AppResponse res = new AppResponse();
        res.setStatus(statusCode);
        res.setPath(path);
        res.setError(error);
        res.setData(data);
        res.setMessage(message);
        return res;
    }

    public static AppResponse buildResponse(HttpStatus httpStatus, Object data) {
        AppResponse res = new AppResponse();
        res.setStatus(httpStatus.value());
        res.setMessage(httpStatus.name());
        res.setData(data);
        return res;
    }

    public AppResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }
}
