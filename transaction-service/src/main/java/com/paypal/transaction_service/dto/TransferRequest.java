package com.paypal.transaction_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransferRequest {
    private Long senderId;
    private Long receiverId;
    private Double amount;
}
