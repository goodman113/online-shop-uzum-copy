package project.service;

import jakarta.persistence.EntityNotFoundException;
import project.mapper.OrderItemMapper;
import project.model.Order;
import project.model.OrderItem;
import project.model.Product;
import project.model.User;
import project.model.create.OrderCreateDto;
import project.model.create.OrderItemCreateDto;
import project.model.dto.OrderDto;
import project.model.dto.OrderItemDto;
import project.model.enums.OrderItemStatus;
import org.springframework.stereotype.Service;
import project.model.enums.OrderStatus;
import project.repository.repository.OrderItemRepository;
import project.repository.repository.OrderRepository;
import project.repository.repository.ProductRepository;
import project.repository.repository.UserRepository;
import project.validator.OrderItemValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderItemService  extends AbstractService
        <OrderItemRepository, OrderItemMapper, OrderItemValidator>
        implements CrudService<OrderItemDto, OrderItemCreateDto,OrderItemDto, Long> {
    final OrderService orderService;
    final UserRepository userRepository;
    final OrderRepository orderRepository;
    final ProductRepository productRepository;
    protected OrderItemService(OrderItemRepository repository, OrderItemMapper mapper, OrderItemValidator validator, OrderService orderService, UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        super(repository, mapper, validator);
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderItemDto create(OrderItemCreateDto dto) {
        validator.onCreate(dto);
        OrderItem orderItem = mapper.fromCreateDto(dto);
        orderItem.setOrder(orderService.getOrderIdFromUser());
        return mapper.toDto(repository.save(orderItem));
    }

    @Override
    public OrderItemDto get(Long id) {
        OrderItem orderItem = repository.findOrderItemById(id).orElseThrow(() -> new EntityNotFoundException("wrong id given"));
        return mapper.toDto(orderItem);
    }

    @Override
    public List<OrderItemDto> getAll(String search) {

        return mapper.toDtoList(repository.findAllByStatus(OrderItemStatus.valueOf(search)));
    }

    @Override
    public OrderItemDto update(OrderItemDto dto) {
        return null;
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void addToUserCart(Long id, Long productId, Integer qty) {
        User user = userRepository.findUserByIdIsAndDeleted(id, false)
                .orElseThrow(() -> new EntityNotFoundException("wrong user id given"));
        List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user,OrderStatus.PENDING);
        putOrderItemToOrder(id, productId, qty, orders);
    }

    public void putOrderItemToOrder(Long id, Long productId, Integer qty, List<Order> orders) {
        Order order = getOrder(id, orders);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("wrong id given"));
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(qty);
        orderItem.setPrice(product.getPrice()* qty);
        orderItem.setStatus(OrderItemStatus.PENDING);
        OrderItem save = repository.save(orderItem);
        order.getItems().add(save);
        order.setTotalPrice(order.getTotalPrice()+save.getPrice());
        orderRepository.save(order);
    }

    public Order getOrder(Long id, List<Order> orders) {
        Order order = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING)
                .findFirst().orElse(null);
        if (order == null) {
            OrderCreateDto orderCreateDto = new OrderCreateDto();
            orderCreateDto.setOrderItemIds(new ArrayList<>());
            orderCreateDto.setBuyerId(id);
            orderCreateDto.setTotalPrice(0d);
            OrderDto orderDto = orderService.create(orderCreateDto);
            order = orderRepository.findById(orderDto.getId()).orElseThrow(() -> new EntityNotFoundException("wrong id given"));
        }
        return order;
    }

    public Map<Long, Integer> getProductsAndQtyOfOrder(Order order) {
        Map<Long, Integer> map = new HashMap<>();
        for (OrderItem orderItem : order.getItems()) {
            map.put(orderItem.getProduct().getId(), orderItem.getQuantity());
        }
        return map;
    }

    public void removeItemFromOrder(User user, Long productId) {
        List<Order> orders = orderRepository.findOrdersByCustomerAndStatus(user,OrderStatus.PENDING);
        Order order = getOrder(user.getId(), orders);
        System.out.println(order);
        order.getItems().removeIf(orderItem -> orderItem.getProduct().getId().equals(productId));
        orderRepository.save(order);
    }
}
