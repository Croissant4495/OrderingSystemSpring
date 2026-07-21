package com.ejada.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.project.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
