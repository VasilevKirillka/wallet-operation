package com.example.account_transactions.demo.dto;

import com.example.account_transactions.demo.entity.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletOperation {
    private UUID walletId;
    private OperationType operationType;
    private Integer amount;
}
