package com.example.clothes_website.repository;

import com.example.clothes_website.entity.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Messages, Integer> {
    @Query("""
        select m from Messages m
        where m.conversation.conversationId = :conversationId
    """)
    Page<Messages> getAllMessageInConversation(@Param("conversationId") int conversationId, Pageable pageable);

    @Modifying
    @Query("""
        update Messages m
        set m.isRead = true
        where m.conversation.conversationId = :conversationId
        and m.sender.userId <> :userId
        and m.isRead = false
    """)
    int markMessagesAsReadInConversation (@Param("conversationId") int conversationId, @Param("userId") int userId);
}
