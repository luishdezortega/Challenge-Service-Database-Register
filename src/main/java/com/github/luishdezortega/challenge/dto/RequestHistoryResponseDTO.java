package com.github.luishdezortega.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.luishdezortega.challenge.model.CallLogEntity;

import java.util.List;

public record RequestHistoryResponseDTO(
        @JsonProperty("records") List<CallLogEntity> callLogEntityList, int numberOfElements, int pageNumber, int pageSize, long totalElements,
        int totalPages) {
}
