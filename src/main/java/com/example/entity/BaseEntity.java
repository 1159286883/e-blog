package com.example.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {

    private long id;
    private Date created;
    private Date modified;
}
