package com.example.grpc.client;

import com.example.grpc.proto.MemberProto;
import com.example.grpc.proto.MemberServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.springframework.stereotype.Component;

/*
* gRPC 클라이언트
* GrpcClient 클래스는 gRPC 클라이언트를 구현한 것으로, 다른 서버 또는 같은 서버내에서 gRPC 서버 메서드 호출 하는데 사용
* 이 클래스는 애플리케이션 내에서 gRPC 서버에 요청을 보내는 역할 함
*/

@Component
public class GrpcMemberClient {

    private final MemberServiceGrpc.MemberServiceBlockingStub blockingStub;

    // gRPC 서버에 연결 - 생성자
    public GrpcMemberClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        blockingStub = MemberServiceGrpc.newBlockingStub(channel);
    }

    /*
    * 회원 생성
    */
    public MemberProto.MemberCreateResponse createMember(MemberProto.MemberRequest request){
        return blockingStub.createMember(request);
    }


}
