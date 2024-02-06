package com.datamodule.repository;

import com.datamodule.models.PublishKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "PublishKeysRepository")
public interface PublishKeysRepository extends JpaRepository<PublishKey,Long> {

}
