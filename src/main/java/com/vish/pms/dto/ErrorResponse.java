package com.vish.pms.dto;

public record ErrorResponse(
    String statusCode,
    String message
) {

}
