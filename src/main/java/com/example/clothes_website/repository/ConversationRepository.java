package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Conversations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversations, Integer> {
    @Query("""
        select c from Conversations c
        where c.customer.userId = :customerId
    """)
    Optional<Conversations> findConversationsByCustomerId(@Param("customerId") int customerId);
}
