package project.sync;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import project.model.Order;
import project.model.enums.OrderStatus;
import project.repository.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusSync {
    final OrderRepository orderRepository;


    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void orderSync(){
        LocalDateTime now = LocalDateTime.now().minusMinutes(5);
        int readyForPickup = orderRepository.updateStatus(OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP,now);
        int preparing = orderRepository.updateStatus(OrderStatus.ACCEPTED, OrderStatus.PREPARING,now);
        log.info("ready for Pickup count: {}", readyForPickup);
        log.info("Preparing count: {}", preparing);
    }
}
