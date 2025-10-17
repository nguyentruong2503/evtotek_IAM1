package com.example.iam1.repository;

import com.example.iam1.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.relation.Role;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByCode(String code);
}
