package com.learninghero.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {

    private Long total;
    private Integer page;
    private Integer size;
    private List<T> records;
}
