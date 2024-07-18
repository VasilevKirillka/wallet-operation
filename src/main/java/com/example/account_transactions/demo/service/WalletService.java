package com.example.account_transactions.demo.service;

import com.example.account_transactions.demo.dto.WalletOperation;
import com.example.account_transactions.demo.entity.WalletEntity;
import com.example.account_transactions.demo.exception.NotFoundException;
import com.example.account_transactions.demo.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.account_transactions.demo.entity.OperationType.DEPOSIT;
import static com.example.account_transactions.demo.entity.OperationType.WITHDRAW;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;
    private final ReentrantLock lock = new ReentrantLock();

    @Transactional
    public ResponseEntity<String> changeBalance(WalletOperation operation) {
        lock.lock();
        try {
            if (isValid(operation)) {
                var wallet = getWalletEntity(operation.getWalletId());
                if (operation.getOperationType().equals(DEPOSIT)) {
                    return deposit(operation, wallet);
                } else if (operation.getOperationType().equals(WITHDRAW)) {
                    return withdraw(operation, wallet);
                }
            }
            log.warn("Операция не выполнена. Проверьте введеные данные: {}", operation);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Операция не выполнена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public ResponseEntity<?> getBalance(UUID id) {
        lock.lock();
        try {
            WalletEntity wallet = getWalletEntity(id);
            log.info("Баланс на кошельке: {}", wallet.getBalance());
            return ResponseEntity.status(HttpStatus.OK).body(wallet);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private ResponseEntity<String> deposit(WalletOperation operation, WalletEntity wallet) {
        log.info("Пополнение кошелька: {} на сумму {}", wallet.getWalletId(), operation.getAmount());
        wallet.setBalance(wallet.getBalance() + operation.getAmount());
        walletRepository.save(wallet);
        return ResponseEntity.ok("Пополнение выполнено успешно");
    }

    private ResponseEntity<String> withdraw(WalletOperation operation, WalletEntity wallet) {
        if (wallet.getBalance() >= operation.getAmount()) {
            log.info("Снятие денег из кошелька: {} на сумму {}", wallet.getWalletId(), operation.getAmount());
            wallet.setBalance(wallet.getBalance() - operation.getAmount());
            walletRepository.save(wallet);
            return ResponseEntity.ok("Снятие выполнено успешно");
        } else {
            log.warn("Недостаточно средств в кошельке: {}, баланс {}, сумма снятия {}",
                    wallet.getWalletId(), wallet.getBalance(), operation.getAmount());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Недостаточно средств");
        }
    }

    private boolean isValid(WalletOperation operation) {
        return operation.getWalletId() != null
                && operation.getOperationType() != null
                && operation.getAmount() != null;
    }

    private WalletEntity getWalletEntity(UUID id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Кошелек не найден"));
    }

}
