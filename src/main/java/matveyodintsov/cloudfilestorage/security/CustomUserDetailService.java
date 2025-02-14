package matveyodintsov.cloudfilestorage.security;

import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByLogin(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        System.out.println(username);
        return User.builder()
                .username(user.getLogin())
                .password(user.getPassword())
                .roles("USER")
                .build();

    }
}
