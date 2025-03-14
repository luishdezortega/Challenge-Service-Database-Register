package com.github.luishdezortega.challenge.service;

import com.github.luishdezortega.challenge.dto.CallLogDTO;
import com.github.luishdezortega.challenge.dto.RequestHistoryResponseDTO;

public interface ICallLogService {

    RequestHistoryResponseDTO getCallLogs(int page, int size, String sort);

    void saveCallLogs(CallLogDTO callLog);

}
