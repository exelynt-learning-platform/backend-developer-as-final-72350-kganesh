package com.ganesh.booking_system.repository;

import com.ganesh.booking_system.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource,Long> {

}