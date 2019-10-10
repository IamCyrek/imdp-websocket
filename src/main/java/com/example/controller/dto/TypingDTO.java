package com.example.controller.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TypingDTO {

    @NotNull
    @Size(max = 63)
    private String userName;

    @NotNull
    private Boolean isTyping;
}
