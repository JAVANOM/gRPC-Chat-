package com.example.grpc.entity;


import com.example.grpc.controller.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="name")
    private String name;

    //factory method
    //Member 엔티티 객체를 매개변수로 받아 해당 객체의 id, email, name 정보를 사용하여 새로운 MemberDTO 객체를 생성
    public static MemberDTO fromEntity(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }
}
