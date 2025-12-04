package project.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.model.PaymentVerificationCode;

import java.util.List;
import java.util.Optional;

public interface PaymentVerificationRepository
        extends JpaRepository<PaymentVerificationCode, Long> {

    Optional<PaymentVerificationCode> findByEmailAndOrderIdAndUsedFalse(String email, Long orderId);

    List<PaymentVerificationCode> findPaymentVerificationCodesByEmail(String email);
}
