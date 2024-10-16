package com.example.grpc.service;


import com.example.grpc.controller.dto.MemberDTO;
import com.example.grpc.controller.dto.MemberSignUpRequestDTO;
import com.example.grpc.entity.Member;
import com.example.grpc.mapper.MemberMapper;
import com.example.grpc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;


    /*
    * @memberDTO 회원 가입 요청
    * @return 회원 엔티티
    * @apiNote 회원 가입을 처리하는 메서드
    */
    public Member craeteMember(MemberSignUpRequestDTO memberDTO) {
        Member member = memberMapper.dtoToEntity(memberDTO);
        return memberRepository.save(member);
    }

    /*
    *  @apiNote 회원 등록하는 메서드
    * */
    public void registerUser(String name, String password, String email) {

        Member member  = Member.builder()
                .name(name)
                .password(password) // 암호화 X
                .email(email)
                .build();

        //데이터베이스에 저장
        memberRepository.save(member);
    }

    /*
    * @apiNote 이메일과 비밀번호로 로그인하는 메서드
    * */
    public MemberDTO loginUser(String email, String password) {
        Member member = memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password?????"));

        return Member.fromEntity(member);
    }
}
