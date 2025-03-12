package com.github.luishdezortega.challenge.repository;

import com.github.luishdezortega.challenge.model.CallLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallLogRepository extends JpaRepository<CallLogEntity, Long> {

    Page<CallLogEntity> findAll(Pageable pageable);

}