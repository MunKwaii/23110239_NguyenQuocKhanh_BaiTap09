package vn.iostar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iostar.entity.User;
import vn.iostar.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public User authenticate(String email, String rawPassword) {
        User u = userRepo.findByEmail(email).orElse(null);
        if (u == null) return null;
        if (!encoder.matches(rawPassword, u.getPassword())) return null;
        return u;
    }
}
