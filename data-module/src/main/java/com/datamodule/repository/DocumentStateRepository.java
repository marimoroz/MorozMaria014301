package com.datamodule.repository;

import com.datamodule.models.DocumentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "DocumentStateRepository")
public interface DocumentStateRepository extends JpaRepository<DocumentState,Long> {
}
