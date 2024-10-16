package com.example.grpc.repository.dsl;

import com.example.grpc.entity.ChatRoomEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.grpc.entity.QChatRoomEntity.chatRoomEntity;

@RequiredArgsConstructor
@Repository
public class ChatRoomDslRepository {

    private final JPAQueryFactory queryFactory;


    /*
    * 모든 비공개 채팅방 조회
    * */
    public List<ChatRoomEntity> findAllChatRoomsPrivate(){
        return queryFactory
                .select(chatRoomEntity)
                .from(chatRoomEntity)
                .where(chatRoomEntity.isPrivateYN.eq("Y"))
                .fetch();
    }

    /*
    @apiNote 비공개 채팅방을 제외한 모든 채팅방 조회
    * */
    public List<ChatRoomEntity> findAllChatRoomsNotPrivate(){
        return queryFactory
                .select(chatRoomEntity)
                .from(chatRoomEntity)
                .where(chatRoomEntity.isPrivateYN.eq("N"))
                .fetch();
    }
}
