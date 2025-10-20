package com.example.iam1.repository;

import com.example.iam1.model.OTP;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends CrudRepository<OTP, String> {
}
