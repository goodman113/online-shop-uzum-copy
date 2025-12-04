package project.model.dto;


import lombok.*;
import project.model.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private VendorDto vendorDto;
    private String phone;
    private LocalDateTime createdAt;
    private List<OrderDto> order = new ArrayList<>();
    private List<ReviewDto> review = new ArrayList<>();
}
