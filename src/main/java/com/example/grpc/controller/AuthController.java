package com.example.grpc.controller;

import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
public class AuthController {

    private final MemberService memberService;

    /*
    * @apiNote 로그인 페이지를 렌더링하는 메서드
    * */
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    /*
    * @apiNote 로그인 요청을 처리하는 메서드
    * */

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session, Model model){

        try {
            // 로그인 성공 시 세션에 user를 저장하고 채팅 페이지로 리다이렉트
            MemberDTO memberDTO = memberService.loginUser(email, password);
            session.setAttribute("user", memberDTO);
            session.getAttribute("user").toString();
            return "redirect:/chat/get-rooms";
        }catch(Exception e){
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }


    /*
    * @apiNote 회원가입 페이지를 렌더링하는 메서드
    * */

    @GetMapping("/register")
    public String registerForm(){
        return "register";
    }

    /*
    * @apiNote 회원가입 요청을 처리하는 메서드
    * */

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password, @RequestParam String name, Model model){
        try {
            memberService.registerUser(email, password, name);
            return "redirect:/login";
        }catch (Exception e){
            model.addAttribute("error", "An error occured during registration");
            return "register";
        }
    }

    /*
    * @apiNote 로그아웃 요청을 처리하는 메서드
    * */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login";
    }
}
