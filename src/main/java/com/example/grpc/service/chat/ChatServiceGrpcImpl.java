package com.example.grpc.service.chat;

import com.example.grpc.chat.ChatMessage;
// .proto 파일을 컴파일하여 생성
import com.example.grpc.chat.ChatServiceGrpc;
import com.example.grpc.controller.chat.ChatRoom;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
* 채팅 Grpc 서비스 구현체
* ChatServiceImplBase : ChatServiceGrpc 클래스의 내부 정적 클래스, 이 클래스는 gRPC 서비스의 기본 구현을 제공하는
* 추상 클래스, 해당 클래스에 정의된 메서드를 구현 해야함
* */
public class ChatServiceGrpcImpl extends ChatServiceGrpc.ChatServiceImplBase {
    private Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();

    /*
    * @apiNote 채팅 메시지를 주고받는 메서드
    * @responseObserver 현재 클라이언트를 의미
    * */
    @Override
    public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {

        // 초기에는 채팅방 ID를 요구하도록 한다.
        return new StreamObserver<ChatMessage>() {

            // 클라이언트로부터 메시지를 전달받으면서 모든 클라이언트에게 전달 한다.
            private String currentRoomId;

            @Override
            public void onNext(ChatMessage chatMessage) {
                if (currentRoomId == null) {
                    currentRoomId = chatMessage.getMessage(); //첫 메시지를 채팅방 ID로 사용
                    ChatRoom chatRoom = chatRooms.computeIfAbsent(currentRoomId, ChatRoom::new); // 채팅방이 없으면 생성
                    chatRoom.join(responseObserver);
                } else {
                    ChatRoom chatRoom = chatRooms.get(currentRoomId);
                    if (chatRoom != null) {
                        chatRoom.broadcast(chatMessage);
                    }
                }

            }

            // 클라이언트로부터 에러가 발생하면 해당 클라이언트를 제거
            @Override
            public void onError(Throwable throwable) {
                ChatRoom chatRoom = chatRooms.get(currentRoomId);
                if(chatRoom != null){
                    chatRoom.leave(responseObserver);
                }
            }

            // 클라이언트로부터 완료 신호를 받으면 해당 클라이언트를 제거(클라이언트가 더 이상 메시지를 보내지 않겠다라는 신호를 보냄)
            @Override
            public void onCompleted() {
                ChatRoom chatRoom = chatRooms.get(currentRoomId);
                if (chatRoom != null) {
                    chatRoom.leave(responseObserver);
                }
                // 클라이언트가 더 이상 메시지를 받을 수 없음을 의미
                responseObserver.onCompleted();
            }

        };
    }
}