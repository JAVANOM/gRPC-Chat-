package com.example.grpc.controller.chat;

import com.example.grpc.controller.dto.ChatRoomResponseDTO;
import com.example.grpc.controller.dto.CreateRoomRequestDTO;
import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.service.chat.ChatRoomService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/get-rooms")
    public String getRooms(Model model, HttpSession session) {
        List<ChatRoomResponseDTO> chatRooms = chatRoomService.findAllChatRooms();
        MemberDTO member = (MemberDTO)session.getAttribute("user");

        if (member != null){
            model.addAttribute("user", member.getName());
            System.out.println("1 : " + member.getName() +": chk");
        }

        model.addAttribute("rooms", chatRooms);
        System.out.println("2 : " + chatRooms +": chk");

        return "rooms";

    }

    @PostMapping("/create-room")
    public String createRoom(@ModelAttribute CreateRoomRequestDTO requestDTO,
                             HttpSession session){
        MemberDTO member = (MemberDTO)session.getAttribute("user");
        if(member != null){
            chatRoomService.createChatRoom(requestDTO, member.getEmail());
        }

        return "redirect:/chat/get-rooms";
    }



}
