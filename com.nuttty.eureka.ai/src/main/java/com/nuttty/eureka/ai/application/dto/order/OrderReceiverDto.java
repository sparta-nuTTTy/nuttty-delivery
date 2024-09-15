package com.nuttty.eureka.ai.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReceiverDto {
    private UUID receiverId;
    private String receiverName;
}
