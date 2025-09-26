package com.assistant.session.repository;

import java.util.List;
import java.util.Optional;

import com.assistant.session.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, String>{
    List<Session> findByUser(Long userId);
}

