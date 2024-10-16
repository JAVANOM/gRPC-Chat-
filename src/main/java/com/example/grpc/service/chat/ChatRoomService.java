package com.example.grpc.service.chat;

import com.example.grpc.controller.dto.ChatRoomResponseDTO;
import com.example.grpc.controller.dto.CreateRoomRequestDTO;
import com.example.grpc.entity.ChatRoomEntity;
import com.example.grpc.entity.Member;
import com.example.grpc.repository.ChatRoomRepository;
import com.example.grpc.repository.MemberRepository;
import com.example.grpc.repository.dsl.ChatRoomDslRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomDslRepository chatRoomDslRepository;


    /*
    * @apiNote 채팅방 생성
    * */
    public void createChatRoom(CreateRoomRequestDTO requestDTO, String creatorEmail) {
        Member creator = memberRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ChatRoomEntity chatRoom = ChatRoomEntity.of(requestDTO.getRoomName(), requestDTO.getIsPrivateYN(), creator, LocalDateTime.now());

        ChatRoomEntity savedChatRoom = chatRoomRepository.save(chatRoom);

        ChatRoomResponseDTO.entityToResponseDto(savedChatRoom);
    }

    /*
    * @apiNote 채팅방 목록 조회
    * */

    public List<ChatRoomResponseDTO> findAllChatRooms() {

        //비공개 채팅방은 제외 (true면 비공개가 아닌 채팅방 조회, false이면 비공개 채팅방 조회)
        List<ChatRoomEntity> chatRoomEntityList = chatRoomDslRepository.findAllChatRoomsNotPrivate();

        // ChatRoomEntity 객체를 ChatRoomResponseDTO 객체로 변환하는 과정을 간편하게 표현
        return chatRoomEntityList.stream()
                .map(ChatRoomResponseDTO::entityToResponseDto)
                .toList();
    }

}
