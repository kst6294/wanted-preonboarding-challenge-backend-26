package com.market.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorResponse {

    private HttpStatusCode status;
    private String code;
    private Validation validation;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Validation {

        private Map<String, String> fields = new HashMap<>();

        public void add(String field, String defaultMessage) {
            fields.put(field, defaultMessage);
        }
    }
}
