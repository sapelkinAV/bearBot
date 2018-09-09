package com.sapelkinav.bear.model;

import lombok.Data;

@Data
public class Request {
    int page;
    String tags;

    public Request(int page, String tags) {
        this.page = page;
        this.tags = tags;
    }
}
