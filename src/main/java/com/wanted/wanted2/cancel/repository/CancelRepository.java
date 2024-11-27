package com.wanted.wanted2.cancel.repository;

import com.wanted.wanted2.cancel.model.CancelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelRepository extends JpaRepository<CancelEntity, Long> {
}
