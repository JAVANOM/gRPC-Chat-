package com.example.grpc.client.chat;

import com.example.grpc.chat.ChatMessage;
import com.example.grpc.chat.ChatServiceGrpc;
import com.example.grpc.entity.ChatMessageEntity;
import com.example.grpc.entity.Member;
import com.example.grpc.repository.ChatMessageRepository;
import com.example.grpc.repository.MemberRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Grpc 채팅 클라이언트
 */
@RequiredArgsConstructor
@Service
public class GrpcChatClient {

    // @GrpcClient("chat-server") : gRPC 서버 이름
    // ChatServiceGrpc.ChatServiceStub : 클라이언트가 서버에 요청을 보내고 응답을 받을 수 있도록 해줌
    @GrpcClient("chat-server")
    private ChatServiceGrpc.ChatServiceStub chatServiceStub;

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;

    // emitters : 각 채팅방에 대한 SseEmitter 리스트를 저장, 이 구조는 여러 클라이언트가 동일한 채팅방에 연결 가능하도록 함
    // ConcurrentHashMap : 여러 스레드가 동시에 접근하더라도 안전하게 데이터를 관리 가능(여러 클라이언트가 동시에 연결될 때 융ㅅㅇ)
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();



    /*
    * @apiNote 클라이언트가 새로고침으로 연결이 끊어질 때, 서버는 해당 클라이언트에 대한 SSE 연결을 정리
    * 이를  위해 SseEmitter에 대해 onCompletion 및 onTimeout 콜백을 설정해 연결이 끊어졌을 때 해당 SseEmitter를 제거
    * */

    public SseEmitter addEmitter(String roomId){

        // 새로운 SseEmitter 생성 (5분 타임아웃 설정) -> 시간 내에 클라이언트가 응답하지 않으면 emitter는 타임아웃 상태
        SseEmitter emitter = new SseEmitter(5*60*1000L);

        // roomId에 해당하는 채팅방의 emitters 리스트를 가져와서 emitter를 추가
        // roomId에 해당하는 리스트가 없다면 ArrayList로 생성 후 emitter를 추가
        emitters.computeIfAbsent(roomId, k -> new ArrayList<>()).add(emitter);

        // 타임아웃 및 완료 핸들러 설정
        // 완료 onCompletion() -> 클라이언트가 연결을 종료했거나, 서버가 해당 emitter를 더 이상 사용하지 않게 되었음을 의미
        emitter.onCompletion(() -> removeEmitter(roomId, emitter));
        emitter.onTimeout(() -> {
            System.out.println("Emitter timed out for roomId : " + roomId);
            removeEmitter(roomId, emitter);
        });

        return emitter;

    }

    /*
    @apiNote 클라이언트에서 주기적으로 ping 메시지를 전송하는 메서드(클라이언트와 서버 간의 연결 상태 확인)
    * */
    @Scheduled(fixedRate = 3000)  //3초마다
    public void sendPingMessages(){
        emitters.forEach((roomId, emitterList) -> {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            for(SseEmitter emitter : emitterList){
                try {
                    // "ping" 이름의 이벤트 이름과 "keep-alive" 라는 이름의 데이터를 이벤트에 담아 전송 -> 활성화 상태 확인
                    emitter.send(SseEmitter.event().name("ping").data("keep-alive"));
                }catch (IOException e){
                    // IOException 발생 시 deadEmitters에 저장하고 실패 메시지 출력
                    deadEmitters.add(emitter);
                    System.out.println("Failed to send ping message to emitter for roomId : " + roomId);
                }
            }
            // emitterList에서 deadEmitters 리스트에 있는 모든 SseEmitter를 제거 -> 메모리 누수 방지
            emitterList.removeAll(deadEmitters);
        });
    }

    /*
    @apiNote sseEmitter를 제거하는 메서드
    * */
    private void removeEmitter(String roomId, SseEmitter emitter) {
        List<SseEmitter> roomEmitters = emitters.get(roomId);
        if(roomEmitters != null) roomEmitters.remove(emitter);
    }


    /*
    @apiNote 채팅메세지를 전송하는 메서드
    * */
    public void sendMessage(String roomId, String userEmail, String message){
        // 사용자 이메일로 사용자 조회
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with email : " + userEmail));

        // ChatMessage 생성
        ChatMessage chatMessage = ChatMessage.newBuilder()
                .setUser(member.getName())
                .setMessage(message)
                .build();

        // gRPC를 통해 메시지를 서버로 전송(chatServiceStub.chat 메서드를 사용하여 gRPC 스트링밍을 시작)
        // StreamObserver<ChatMessage>를 통해 gRPC 서버로 메시지를 전송, 서버에서 수신된 메시지를 처리
        StreamObserver<ChatMessage> requestObserver  = chatServiceStub.chat(new StreamObserver<ChatMessage>() {

            @Override
            public void onNext(ChatMessage value) {
                // gRPC로부터 메시지를 수신했을 때의 로직
                // (서버에서 메시지를 수신했을 때 호출되며, 이 메시지를 채팅방의 클라이언트에게 브로드캐스트)
                broadcastToClients(roomId, value);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("gRPC server error : " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("gRPC server completed");
            }
        });

        // chatMessage를 gRPC 서버로 전송
        requestObserver.onNext(chatMessage);

        // 필요에 따라 스트림을 완료
        // requestObserver.onCompleted();

        // ChatMessageEntity 생성
        ChatMessageEntity chatMessageEntity = ChatMessageEntity.builder()
                .roomId(roomId)
                .member(member)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        // 메시지 저장
        chatMessageRepository.save(chatMessageEntity);

    }

    /*
    @apiNote 클라이언트에게 메시지를 브로드캐스트 하는 메서드
    * */

    private void broadcastToClients(String roomId, ChatMessage message) {
        // 1. roomId에 해당하는 채팅방의 emitters를 가져옮
        List<SseEmitter> roomEmitters = emitters.get(roomId);

        // 2. emitters가 null이 아니라면, emmitters에 있는 모든 emitter에게 메시지를 전송
        if(roomEmitters != null) {
            // deadEmitters 리스트를 생성 (deadEmitters: 메시지 전송에 실패한 emitter를 저장하는 리스트) -> 추적하여 제거
            List<SseEmitter> deadEmitters = new ArrayList<>();

            //roomEmitters에 있는 모든 emitter에게 메시지를 전송
            for (SseEmitter emitter : roomEmitters) {
                try {
                    emitter.send(SseEmitter.event().data(message.getUser() + " : " + message.getMessage()));
                }catch(Exception e){
                    deadEmitters.add(emitter);
                }
            }
            // 문제 있는 Emitter들을 roomEmitters에서 제거
            for (SseEmitter deadEmitter : deadEmitters) {
                removeEmitter(roomId, deadEmitter);
            }
        }
    }



}
