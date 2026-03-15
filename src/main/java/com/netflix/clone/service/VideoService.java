package com.netflix.clone.service;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.netflix.clone.dto.request.VideoRequest;
import com.netflix.clone.dto.response.MessageResponse;
import com.netflix.clone.dto.response.PageResponse;
import com.netflix.clone.dto.response.VideoResponse;
import com.netflix.clone.dto.response.VideoStatsReponse;

import jakarta.validation.Valid;

public interface VideoService {

   MessageResponse createVideoByAdmin(@Valid VideoRequest videoRequest);

   PageResponse<VideoResponse> getAllAdminVideos(int page, int size, String search);
   MessageResponse updatedVideoByAdmin(Long id, @Valid VideoRequest videoRequest);
   MessageResponse deleteVideoByAdmin(Long id);
   MessageResponse toggleVideoByAdmin(Long id, boolean value);

   VideoStatsReponse getAdminStats();

   PageResponse<VideoResponse> getPublishedVideos(int page, int size, String search, String email);

   List<VideoResponse> getFeaturedVideos();
}
