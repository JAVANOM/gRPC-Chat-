package com.example.grpc.controller.dto;


import com.example.grpc.entity.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatRoomResponseDTO {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private MemberDTO creator;

    // factory method
    // ChatRoomEntity 객체를 ChatRoomResponseDTO로 변환하는 정적메서드
    public static ChatRoomResponseDTO entityToResponseDto(ChatRoomEntity savedChatRoom) {
        return ChatRoomResponseDTO.builder()
                .id(savedChatRoom.getId())
                .name(savedChatRoom.getName())
                .createdAt(savedChatRoom.getCreatedAt())
                .build();
    }
}
