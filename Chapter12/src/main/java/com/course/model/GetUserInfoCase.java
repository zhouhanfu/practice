package com.course.model;

import lombok.Data;

@Data
public class GetUserInfoCase {
    private int UserId;
    private String expected;
}
