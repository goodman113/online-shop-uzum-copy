package project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.model.User;
import project.repository.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class
CustomUserDetailsService implements UserDetailsService {
    final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        if (!user.isEnabled()||user.getVendorProfile()!=null&&!user.getVendorProfile().isApproved()) {
            throw new UsernameNotFoundException("you were not approved yet");
        }
        System.out.println("loaded user: " + user);
        return org.springframework.security.core.userdetails
                .User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
