package ru.mtuci.praktikaRBPO.services.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mtuci.praktikaRBPO.model.UserDetailsImpl;
import ru.mtuci.praktikaRBPO.repository.UserRepository;

@RequiredArgsConstructor
@Service
public final class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return UserDetailsImpl.fromUser(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found")));
    }

}
