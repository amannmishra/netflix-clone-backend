package com.netflix.clone.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T>{
    private List<T>content;
    private Long totalElements;
    private int totalPages;
    private int number;
    private int size;
}
