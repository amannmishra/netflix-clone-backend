package com.netflix.clone.utils;


import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.netflix.clone.dto.response.PageResponse;

public class PaginationUtils {

    private PaginationUtils(){

    }
    public static Pageable createPageRequest(int page,int size,String sortBy){
        return PageRequest.of(page, size,Sort.by(Sort.Direction.DESC,sortBy));
    }

    public static Pageable createPageRequest(int page,int size){
        return PageRequest.of(page, size);
    }

    public static <T,R> PageResponse<R>toPagePageResponse(Page<T>page,Function<T,R> mapper){
        List<R>content=page.getContent().stream().map(mapper).toList();
        return new PageResponse<>(content,page.getTotalElements(),page.getTotalPages(),page.getNumber(),page.getSize());
    }
    public static <R>PageResponse<R>toPagePageResponse(Page<?> page,List<R>mappedContent){
        return new PageResponse<>(mappedContent,page.getTotalElements(),page.getTotalPages(),page.getNumber(),page.getSize());
    }

}
