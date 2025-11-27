package project.model.create;

import lombok.*;
import project.model.dto.VendorDto;
import project.model.enums.Role;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserCreateDto {
    private String username;
    private String email;
    private String password;
    private Role role;
    private String phone;
    private VendorDto vendor;
    private String confirmPassword;
}
