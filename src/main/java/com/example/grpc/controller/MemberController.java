package com.example.grpc.controller;


import com.example.grpc.client.GrpcMemberClient;
import com.example.grpc.proto.MemberProto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final GrpcMemberClient grpcMemberClient;

    @PostMapping("/create")
    public String createMember() {
    MemberProto.MemberCreateResponse member = grpcMemberClient.createMember(
            MemberProto.MemberRequest
                        .newBuilder()
                        .setEmail("test")
                        .setName("test")
                        .setPassword("test")
                        .build()
        );
        return member.toString();
    }
}
