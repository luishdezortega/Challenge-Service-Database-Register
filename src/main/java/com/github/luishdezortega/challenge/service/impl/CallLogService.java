package com.github.luishdezortega.challenge.service.impl;

import com.github.luishdezortega.challenge.model.CallLogEntity;
import com.github.luishdezortega.challenge.repository.CallLogRepository;
import com.github.luishdezortega.challenge.service.ICallLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CallLogService implements ICallLogService {

    private final CallLogRepository callLogRepository;

    @Override
    public Page<CallLogEntity> getCallLogs(int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        return callLogRepository.findAll(pageable);
    }

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
    }

}
