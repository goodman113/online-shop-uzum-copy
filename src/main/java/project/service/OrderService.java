package project.service;

import project.mapper.OrderMapper;
import project.model.Order;
import project.model.OrderItem;
import project.model.create.OrderCreateDto;
import project.model.dto.OrderDto;
import org.springframework.stereotype.Service;
import project.repository.repository.OrderItemRepository;
import project.repository.repository.OrderRepository;
import project.validator.OrderValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class OrderService  extends AbstractService
        <OrderRepository, OrderMapper, OrderValidator>
        implements CrudService<OrderDto, OrderCreateDto,OrderDto, Long> {
    final OrderItemRepository orderItemRepository;
    protected OrderService(OrderRepository repository, OrderMapper mapper, OrderValidator validator, OrderItemRepository orderItemRepository) {
        super(repository, mapper, validator);
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderDto create(OrderCreateDto dto) {
        validator.onCreate(dto);
        Order order = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto get(Long id) {
        Order order = repository.findUserByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("order not found"));
        return mapper.toDto(order);
    }

    @Override
    public List<OrderDto> getAll(String search) {
        return List.of();
    }

    public List<OrderDto> getAll(String search, LocalDateTime from,  LocalDateTime to) {
       return mapper.toDtoList(repository.findAll(search,from,to));
    }

    @Override
    public OrderDto update(OrderDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Order getOrderIdFromUser() {
        /// from current User get Order id if has
        return null;
    }

    public void updateOrderItemInOrder(Order order, Long productId, Integer newQuantity) {
        OrderItem orderItem = order.getItems().stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), productId))
                .findFirst().orElseThrow(() -> new RuntimeException("product not found"));
        orderItem.setQuantity(newQuantity);
        orderItemRepository.save(orderItem);
        repository.save(order);
    }
}
