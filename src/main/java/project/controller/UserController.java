package project.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import project.mapper.UserMapper;
import project.mapper.VendorMapper;
import project.model.User;
import project.model.Vendor;
import project.model.create.UserCreateDto;
import project.model.dto.UserDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import project.repository.repository.UserRepository;
import project.repository.repository.VendorRepository;
import project.service.UserService;
import project.service.VendorService;

import java.util.List;

@Controller
@RequestMapping()
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class UserController {
    final UserService service;
    @GetMapping("/user")
    public String users(Model model,@RequestParam String username) {
        List<UserDto> all = service.getAll(username);
        model.addAttribute("users", all);
        return "user/users";
    }
    @GetMapping("/user/{id}")
    public String user(@PathVariable String id, Model model) {
        UserDto userDto = service.get(Long.parseLong(id));
        model.addAttribute("user", userDto);
        return "user/userDetails";
    }
    @PostMapping("/user")
    public String user(@RequestBody UserCreateDto userDto) {
        service.create(userDto);
        return "redirect:/user";
    }
    @DeleteMapping("/user/{id}")
    @Transactional
    public String user(@PathVariable String id) {
        service.delete(Long.parseLong(id));
        return "redirect:/user";
    }

}
