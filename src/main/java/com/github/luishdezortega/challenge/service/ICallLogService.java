package com.github.luishdezortega.challenge.service;

import com.github.luishdezortega.challenge.model.CallLogEntity;
import org.springframework.data.domain.Page;

public interface ICallLogService {

    Page<CallLogEntity> getCallLogs(int page, int size, String sort);

}
