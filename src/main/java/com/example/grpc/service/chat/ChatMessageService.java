package com.example.grpc.service.chat;

import com.example.grpc.controller.dto.ChatMessageDTO;
import com.example.grpc.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    /*
    * @apiNote 채팅방의 채팅내역을 조회하는 메서드
    * */

    public List<ChatMessageDTO> getChatMessages(String roomId){
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId).stream()
                .map(entity -> ChatMessageDTO.builder()
                        .userName(entity.getMember().getName())
                        .message(entity.getMessage())
                        .timestamp(entity.getTimestamp())
                        .build()).toList();
    }


}
