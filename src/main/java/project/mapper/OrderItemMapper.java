package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.OrderItem;
import project.model.create.OrderItemCreateDto;
import project.model.dto.OrderItemDto;
import org.springframework.stereotype.Component;
import project.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {
    final ProductMapper productMapper;
    public OrderItem fromCreateDto(OrderItemCreateDto dto) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(dto.getProduct());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setPrice(dto.getPrice());
        orderItem.setStatus(dto.getStatus());
        return orderItem;
    }

    public OrderItemDto toDto(OrderItem save) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(save.getId());
        orderItemDto.setPrice(save.getPrice());
        orderItemDto.setQuantity(save.getQuantity());
        orderItemDto.setProduct(productMapper.toDto(save.getProduct()));
        return orderItemDto;
    }

    public List<OrderItemDto> toDtoList(List<OrderItem> allByStatus) {
        return  allByStatus.stream().map(this::toDto).collect(Collectors.toList());
    }
}
