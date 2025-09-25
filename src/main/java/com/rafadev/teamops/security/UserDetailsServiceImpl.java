package com.rafadev.teamops.security;

import com.rafadev.teamops.domain.User;
import com.rafadev.teamops.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository users;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User u = users.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserPrincipal(u);
    }
}
