package com.example.grpc.controller.chat;

/*
*  Grpc chat Controller
* */

import com.example.grpc.client.chat.GrpcChatClient;
import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.controller.dto.MessageRequestDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/chat")
public class ChatController {

    private final GrpcChatClient grpcChatClient;

    /*
    * @apiNote http://localhost:8080/chat/{roodId}?user={user} 에 접속하면 chat.mustache 템플릿을 렌더링함
    * */
    @GetMapping("/{roomId}")
    public String chatRoom(
            @PathVariable String roomId,
            HttpSession session,
            Model model
    ){
        // 세션에 user가 없으면 로그인 페이지로 리다이렉트(사용자 정보를 저장)
        MemberDTO member = (MemberDTO)session.getAttribute("user");
        if (member == null) {
            return "redirect:/login";
        }

        // roomId와 user를 모델에 추가하여 chat.mustache 템플릿에 전달(뷰에 데이터를 전달하기 위해)
        model.addAttribute("roomId", roomId);
        model.addAttribute("user", member.getName());

        return "chat";

    }

    /*
    * @apiNote http://localhost:8080/chat/stream/{roomId}에 접속하면 메시지를 스트리밍합니다.
    * produces = "text/event-stream" : 해당 메서드가 반환하는 콘텐츠 타입이 "text/event-stream" 임을 명시
    * */
    @GetMapping(value="/stream/{roomId}", produces = "text/event-stream")
    public SseEmitter streamChat(@PathVariable String roomId){
        return grpcChatClient.addEmitter(roomId);
    }


    /*
    * @apiNote http://localhost:8080/chat/send/{roomId} post 요청 - 채팅 메시지 전송
    * */
    /*@PostMapping("/send/{roomId}")
    public ResponseEntity<Void> sendMessage(@PathVariable String roomId,
                                            @RequestBody MessageRequestDTO messageRequestDTO
                                            //@RequestParam String user,
                                            //@RequestParam String message
                                            )
    {
        try {
            // lombok 로깅 라이브러리 사용
            log.info("Received message from " + messageRequestDTO.getUser() +": "+ messageRequestDTO.getMessage());
            //System.out.println("Received message from " + user +": "+ message);
            // 사용자가 보낸 메시지를 전송
            grpcChatClient.sendMessage(roomId, messageRequestDTO.getUser(), messageRequestDTO.getMessage());
            // HTTP 200 OK 상태 코드를 반환, 본문이 없는 응답 생성, 이는 메시지가 성공적으로 전송 되었음을 알림
            return ResponseEntity.ok().build();

        }catch (Exception e){
            // 로그 기록
            log.info("Error sending message : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }*/

    /**
     * @param roomId  채팅방 ID
     * @param user    사용자 이름
     * @param message 메시지
     * @return ResponseEntity<Void>
     * @apiNote http://localhost:8090/chat/send/{roomId} 에 POST 요청을 보내면 채팅 메시지를 전송합니다.
     */
    @PostMapping("/send/{roomId}")
    public ResponseEntity<Void> sendMessage(
            @PathVariable String roomId,
            @RequestParam String user,
            @RequestParam String message
    ) {
        System.out.println("Received message from " + user + ": " + message);
        grpcChatClient.sendMessage(roomId, user, message);
        return ResponseEntity.ok().build();
    }


}
