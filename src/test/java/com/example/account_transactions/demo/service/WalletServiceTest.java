package com.example.account_transactions.demo.service;

import com.example.account_transactions.demo.dto.WalletOperation;
import com.example.account_transactions.demo.entity.WalletEntity;
import com.example.account_transactions.demo.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static com.example.account_transactions.demo.entity.OperationType.DEPOSIT;
import static com.example.account_transactions.demo.entity.OperationType.WITHDRAW;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @InjectMocks
    private WalletService walletService;
    @Mock
    private WalletRepository walletRepository;
    private final static UUID WALLET_ID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");

    @BeforeEach
    void init() {
        walletService = new WalletService(walletRepository);
    }


    @Test
    public void testGetBalance() {
        WalletEntity wallet = new WalletEntity(WALLET_ID, 2000);
        Mockito.when(walletRepository.findById(WALLET_ID)).thenReturn(java.util.Optional.of(wallet));

        var result = walletService.getBalance(WALLET_ID);

        assertEquals(wallet, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetBalanceWithNotExistWallet() {
        Mockito.when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.empty());

        var result = walletService.getBalance(WALLET_ID);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Кошелек не найден", result.getBody());
    }

    @Test
    public void testDeposit() {
        WalletOperation depositOperation = new WalletOperation(WALLET_ID, DEPOSIT, 1000);
        WalletEntity wallet = new WalletEntity(WALLET_ID, 0);
        Mockito.when(walletRepository.findById(WALLET_ID)).thenReturn(java.util.Optional.of(wallet));

        ResponseEntity<String> result = walletService.changeBalance(depositOperation);

        assertEquals("Пополнение выполнено успешно", result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testWithdrawWithSufficientFunds() {
        WalletOperation withdrawOperation = new WalletOperation(WALLET_ID, WITHDRAW, 1000);
        WalletEntity wallet = new WalletEntity(WALLET_ID, 2000);
        Mockito.when(walletRepository.findById(WALLET_ID)).thenReturn(java.util.Optional.of(wallet));

        ResponseEntity<String> result = walletService.changeBalance(withdrawOperation);

        assertEquals("Снятие выполнено успешно", result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testWithdrawWithInsufficientFunds() {
        WalletOperation withdrawOperation = new WalletOperation(WALLET_ID, WITHDRAW, 2000);
        WalletEntity wallet = new WalletEntity(WALLET_ID, 1000);
        Mockito.when(walletRepository.findById(WALLET_ID)).thenReturn(java.util.Optional.of(wallet));

        ResponseEntity<String> result = walletService.changeBalance(withdrawOperation);

        assertEquals("Недостаточно средств", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testChangeBalanceWithInvalidJson() {
        WalletOperation withdrawOperation = new WalletOperation(WALLET_ID, WITHDRAW, null);

        ResponseEntity<String> result = walletService.changeBalance(withdrawOperation);

        assertEquals("Операция не выполнена", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}