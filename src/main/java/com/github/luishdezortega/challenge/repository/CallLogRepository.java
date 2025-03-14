package com.github.luishdezortega.challenge.repository;

import com.github.luishdezortega.challenge.model.CallLogEntity;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallLogRepository extends JpaRepository<CallLogEntity, Long> {

    @NonNull
    Page<CallLogEntity> findAll(@NonNull Pageable pageable);

}