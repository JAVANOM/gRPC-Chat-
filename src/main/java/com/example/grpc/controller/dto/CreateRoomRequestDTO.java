package com.example.grpc.controller.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class CreateRoomRequestDTO {

    private String roomName;
    private String isPrivateYN;
}
