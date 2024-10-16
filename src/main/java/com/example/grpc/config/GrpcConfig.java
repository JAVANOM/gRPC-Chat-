package com.example.grpc.config;

import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    /*
    * gRPC 예외 처리 인터셉터 설정
    * @Configuration을 사용해서 생성한 인터셉터를 "스프링 빈"으로 등록해 준다.
    * @GrpcClobalServerInterceptor 어노테이션 내부 - @Component, @Bean
    * */

    @GrpcGlobalServerInterceptor
    ExceptionHandlingInterceptor exceptionHandlingInterceptor(){
        return new ExceptionHandlingInterceptor();
    }
}
