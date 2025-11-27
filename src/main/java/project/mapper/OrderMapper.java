package project.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import project.model.Order;
import project.model.OrderItem;
import project.model.create.OrderCreateDto;
import project.model.dto.OrderDto;
import org.springframework.stereotype.Component;
import project.model.dto.UserShortDto;
import project.model.enums.OrderStatus;
import project.repository.repository.OrderItemRepository;
import project.repository.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    final UserRepository userRepository;
    final OrderItemRepository orderItemRepository;
    @Lazy
    final OrderItemMapper  orderItemMapper;
    public Order fromCreateDto(OrderCreateDto dto) {
        Order order = new Order();
        order.setCustomer(userRepository.findById(dto.getBuyerId()).orElseThrow(() -> new RuntimeException("buyerId is null")));
        List<OrderItem> orderItems = new ArrayList<>();
        dto.getOrderItemIds()
                .forEach(id -> orderItems.add(orderItemRepository
                        .findById(id).orElseThrow(() -> new RuntimeException("orderItemId is null"))));
        order.setStatus(OrderStatus.PENDING);
        order.setItems(orderItems);
        order.setTotalPrice(dto.getTotalPrice());
        return order;
    }

    public OrderDto toDto(Order save) {
        OrderDto orderDto = new OrderDto();
        orderDto.setPayingMethod(save.getPayingMethod());
        orderDto.setWithСourier(save.getWithСourier());
        orderDto.setAddress(save.getAddress());
        orderDto.setId(save.getId());
        orderDto.setOrderItems(orderItemMapper.toDtoList(save.getItems()));
        orderDto.setTotalPrice(save.getTotalPrice());
        orderDto.setOrderStatus(save.getStatus());
        UserShortDto userShortDto = new UserShortDto();
        userShortDto.setEmail(save.getCustomer().getEmail());
        userShortDto.setId(save.getCustomer().getId());
        userShortDto.setUsername(save.getCustomer().getUsername());
        orderDto.setCostumer(userShortDto);
        return orderDto;
    }

    public List<OrderDto> toDtoList(List<Order> all) {
        return all.stream().map(this::toDto).collect(Collectors.toList());
    }
}
