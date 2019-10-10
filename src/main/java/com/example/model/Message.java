package com.example.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@Table(schema = "websocket")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_name")
    @NotNull
    @Size(max = 63)
    private String userName;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String content;

    @Column(nullable = false, name = "creation_time")
    @NotNull
    private Date creationTime;

    @Column(nullable = false)
    @NotNull
    @Max(9)
    private Long room;

}
