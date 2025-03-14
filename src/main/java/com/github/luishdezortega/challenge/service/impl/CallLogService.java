package com.github.luishdezortega.challenge.service.impl;

import com.github.luishdezortega.challenge.dto.CallLogDTO;
import com.github.luishdezortega.challenge.dto.RequestHistoryResponseDTO;
import com.github.luishdezortega.challenge.exception.BadRequestException;
import com.github.luishdezortega.challenge.exception.DatabaseConnectionException;
import com.github.luishdezortega.challenge.model.CallLogEntity;
import com.github.luishdezortega.challenge.repository.CallLogRepository;
import com.github.luishdezortega.challenge.service.ICallLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CallLogService implements ICallLogService {

    private final CallLogRepository callLogRepository;

    @Override
    public RequestHistoryResponseDTO getCallLogs(int page, int size, String sort) {

        try {
            var pageable = createPageable(page, size, sort);
            var result = callLogRepository.findAll(pageable);

            return new RequestHistoryResponseDTO(
                    result.getContent(),
                    result.getNumberOfElements(),
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages());

        } catch (DataAccessException e) {
            throw new DatabaseConnectionException("Error trying to get the records", e);
        }
    }

    @Async
    @Override
    public void saveCallLogs(CallLogDTO callLog) {
        var logEntry = CallLogEntity.builder()
                .timestamp(callLog.time())
                .endpoint(callLog.route())
                .parameters(callLog.parameters())
                .response(callLog.result())
                .build();
        callLogRepository.save(logEntry);
    }

    private Pageable createPageable(int page, int size, String sort) {
        var validValues = Set.of("timestamp", "asc", "desc");
        var sortField = "timestamp";
        var direction = Sort.Direction.ASC;
        for (String param : sort.split(",")) {
            if (!validValues.contains(param)) {
                throw new BadRequestException("Bad request, Invalid sort field");
            }
            if (param.equalsIgnoreCase("asc") || param.equalsIgnoreCase("desc")) {
                direction = Sort.Direction.fromString(param.toUpperCase());
            } else {
                sortField = param;
            }
        }
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

}
