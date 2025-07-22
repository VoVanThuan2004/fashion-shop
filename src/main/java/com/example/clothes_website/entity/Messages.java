package com.example.clothes_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @Column(columnDefinition = "TEXT")
    private String textMessage;

    private String messageType;
    private boolean isRead;
    private LocalDateTime sendTime;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Users sender;

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ImageMessages> imageMessages;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversations conversation;
}
