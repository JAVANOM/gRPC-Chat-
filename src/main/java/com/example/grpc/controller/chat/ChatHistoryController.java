package com.example.grpc.controller.chat;


import com.example.grpc.controller.dto.ChatMessageDTO;
import com.example.grpc.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatHistoryController {

    private final ChatMessageService chatMessageService;

    /*
    *  @apiNote 채팅방의 채팅내역을 조회하는 메서드
    * */
    @GetMapping("/history/{roomId}")
    public List<ChatMessageDTO> getChatHistory(@PathVariable String roomId){
        return chatMessageService.getChatMessages(roomId);
    }
}
