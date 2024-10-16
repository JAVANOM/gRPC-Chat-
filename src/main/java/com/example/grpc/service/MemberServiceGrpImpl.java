package com.example.grpc.service;

import com.example.grpc.controller.dto.MemberSignUpRequestDTO;
import com.example.grpc.entity.Member;
import com.example.grpc.mapper.MemberMapper;
import com.example.grpc.proto.MemberProto;
import com.example.grpc.proto.MemberServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class MemberServiceGrpImpl extends MemberServiceGrpc.MemberServiceImplBase {

    private final MemberService memberService;
    private final MemberMapper memberMapper;


    @Override
    public void createMember(MemberProto.MemberRequest request,
                             StreamObserver<MemberProto.MemberCreateResponse> responseObserver) {

        // 인터셉터 테스트
        // if(request.getEmail().contains("test")){
        //    throw new IllegalArgumentException("Test exception: Invalid email");
        // }

        // 1. 클라이언트로부터 전달받은 request 데이터를 DTO로 변환
        MemberSignUpRequestDTO memberDTO = memberMapper.requestProtoToDto(request);

        // 2. 서비스 레이어에서 request 데이터를 사용해서 RDB에 저장하는 로직을 수행하고 결과를 받는다
        Member createMember = memberService.craeteMember(memberDTO);

        // 3. RDB에 저장된 데이터를 gRPC response 데이터로 변환
        MemberProto.MemberCreateResponse response = memberMapper.dtoToResponseProto(createMember);

        // 4. 응답을 클라이언트에게 전달
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
