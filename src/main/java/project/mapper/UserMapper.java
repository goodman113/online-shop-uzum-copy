package project.mapper;

import lombok.RequiredArgsConstructor;
import project.model.Order;
import project.model.Review;
import project.model.User;
import project.model.create.UserCreateDto;
import project.model.dto.UserDto;
import org.springframework.stereotype.Component;
import project.model.dto.VendorDto;
import project.repository.repository.OrderRepository;
import project.repository.repository.ProductRepository;
import project.repository.repository.ReviewRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {
    final VendorMapper vendorMapper;
    final ReviewMapper reviewMapper;
    final OrderMapper orderMapper;
    final ProductMapper productMapper;
    final ProductRepository productRepository;

    public UserDto toDto(User receiver, List<Review> reviews, List<Order> orders) {
        UserDto userDto = new UserDto();
        userDto.setUsername(receiver.getUsername());
        userDto.setEmail(receiver.getEmail());
        userDto.setPhone(receiver.getPhone());
        userDto.setId(receiver.getId());

        userDto.setCreatedAt(receiver.getCreatedAt());
        if (receiver.getVendorProfile() != null) {
            VendorDto vendorDto = vendorMapper.toDto(receiver.getVendorProfile());
            vendorDto.setProducts(productMapper.toDtoList(productRepository.findProductsByVendor(receiver.getVendorProfile())));
            userDto.setVendorDto(vendorDto);
        }else
            userDto.setVendorDto(null);
        if (reviews != null) {
            userDto.setReview(reviewMapper.toDtoList(reviews));
        }else
            userDto.setReview(null);
        if (orders != null) {
            userDto.setOrder(orderMapper.toDtoList(orders));
        }else
            userDto.setOrder(null);
        return userDto;
    }

    public User fromCreateDto(UserCreateDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setEnabled(true);
        user.setUsername(dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setVendorProfile(dto.getVendor() != null ?vendorMapper.fromDto(dto.getVendor()): null);
        return user;
    }

    public List<UserDto> toDtoList(List<User> all) {
        return all.stream().map(user -> toDto(user,null,null)).collect(Collectors.toList());
    }
}
