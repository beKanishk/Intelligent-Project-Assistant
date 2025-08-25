package com.assistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assistant.model.Message;
import com.assistant.model.Session;

import java.util.List;
import com.assistant.model.User;


public interface MessageRepository extends JpaRepository<Message, Long>{
	List<Message> findBySessionAndUser(Session session, User user);
	boolean existsBySession(Session session);
	
//	@Modifying
//	@Query("DELETE FROM Message m WHERE m.session.id = :sessionId")
//	void deleteBySessionId(@Param("sessionId") String sessionId);

	
	@Modifying
	@Query(value = "DELETE FROM messages WHERE session_id = ?1", nativeQuery = true)
	void deleteBySessionId(String sessionId);
	
	void deleteBySession_Id(String sessionId);

}
