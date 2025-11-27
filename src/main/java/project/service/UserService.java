package project.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import project.mapper.UserMapper;
import project.model.Order;
import project.model.Review;
import project.model.User;
import project.model.create.UserCreateDto;
import project.model.dto.UserDto;
import org.springframework.stereotype.Service;
import project.repository.repository.OrderRepository;
import project.repository.repository.ReviewRepository;
import project.repository.repository.UserRepository;
import project.validator.UserValidator;

import java.util.List;

@Service
public class UserService  extends AbstractService
        <UserRepository, UserMapper, UserValidator>
        implements CrudService<UserDto, UserCreateDto,UserDto, Long> {
    final ReviewRepository reviewRepository;
    final UserRepository userRepository;
    final OrderRepository orderRepository;
    protected UserService(UserRepository repository, UserMapper mapper, UserValidator validator, ReviewRepository reviewRepository, UserRepository userRepository, OrderRepository orderRepository) {
        super(repository, mapper, validator);
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public UserDto create(UserCreateDto dto) {
        validator.onCreate(dto);
        User user = mapper.fromCreateDto(dto);
        return mapper.toDto(repository.save(user),null,null);
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository.findUserByIdIsAndDeleted(id,false).orElseThrow(() -> new RuntimeException("User not found"));
        List<Review> reviews = reviewRepository.findReviewsByCustomer(user);
        List<Order> orders = orderRepository.findOrdersByCustomer(user);
        return mapper.toDto(repository.findById(id).orElseThrow(()-> new RuntimeException("User not found!")),reviews,orders);
    }

    @Override
    public List<UserDto> getAll(String search) {
        return mapper.toDtoList(repository.findAll(search));
    }

    @Override
    public UserDto update(UserDto dto) {
        return null;
    }

    @Override

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user1 = (UserDetails) auth.getPrincipal();
        return userRepository.findByUsername(user1.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
