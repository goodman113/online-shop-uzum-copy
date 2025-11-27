package project.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.model.User;
import project.model.create.UserCreateDto;
import project.model.create.VendorCreateDto;
import project.model.dto.VendorDto;
import project.model.enums.Role;
import project.repository.repository.UserRepository;
import project.service.UserService;
import project.service.VendorService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    final UserService  userService;
    final PasswordEncoder passwordEncoder;
    final VendorService vendorService;
    final AuthenticationManager authenticationManager;

    final RememberMeServices rememberMeServices;
    private final UserRepository userRepository;

    @GetMapping("/login")
    public String loginPage(Model model,HttpServletRequest request) {
        model.addAttribute("currentPath",request.getRequestURI());
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "remember-me",required = false) String rememberMe,
                        HttpServletResponse response,
                        HttpServletRequest request, Model model) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        System.out.println(user.getVendorProfile());
        if (!user.isEnabled()||user.getVendorProfile()!=null&&!user.getVendorProfile().isApproved()) {
            model.addAttribute("messagges","you were not approved yet");
            return "redirect:/login";
        }
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        rememberMeServices.loginSuccess(request,response,authenticate);
        return "redirect:/product";
    }
    @GetMapping("/register")
    public String registerPage(Model model,HttpServletRequest request) {
        model.addAttribute("currentPath",request.getRequestURI());
        model.addAttribute("user", new UserCreateDto());
        return "user/register";
    }
    @PostMapping("/register")
    public String register(Model model, UserCreateDto dto,HttpServletRequest request) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getRole() == Role.VENDOR) {
            VendorCreateDto vendorDto1 = new VendorCreateDto();
            vendorDto1.setDescription(dto.getVendor().getDescription());
            vendorDto1.setShopName(dto.getVendor().getShopName());
            VendorDto vendorDto = vendorService.create(vendorDto1);
            dto.setVendor(vendorDto);
        }else
            dto.setVendor(null);
        userService.create(dto);
        model.addAttribute("currentPath",request.getRequestURI());
        System.out.println("user = " + dto);
        return "user/login";
    }
}


