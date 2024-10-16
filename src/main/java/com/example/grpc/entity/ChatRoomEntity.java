package com.example.grpc.entity;

import com.example.grpc.controller.chat.ChatRoom;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
/*
* @apiNote 채팅방의 응답정보를 담기 위한 데이터 전송 객체
* */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
@Entity
@Table(name = "chat_room")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private Member creator;

    @Column(nullable = false, name = "is_private")
    private String isPrivateYN; // 비공개 여부 Y,N

    //factory method
    public static ChatRoomEntity of(String roomName, String isPrivate, Member creator, LocalDateTime now) {
        return ChatRoomEntity.builder()
                .name(roomName)
                .isPrivateYN(isPrivate)
                .creator(creator)
                .createdAt(now)
                .build();
    }

}
