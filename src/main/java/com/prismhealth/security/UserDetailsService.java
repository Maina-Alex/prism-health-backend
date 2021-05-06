/* package com.prismhealth.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.prismhealth.repository.AccountRepository;
import com.prismhealth.repository.UserRolesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

    @Service
    class UserDetailsServiceImp implements UserDetailsService {

        @Autowired
        private AccountRepository userRepository;
        @Autowired
        private UserRolesRepo roleRepo;

        public UserDetailsServiceImp() {
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            Optional<User> emailUser = userRepository.findOneByPhone(username);
            if (emailUser.isPresent()) {
                List<SimpleGrantedAuthority> grantedAuthorities = roleRepo.findAllByUserId(emailUser.get().getUsername()).stream()
                        .map(SimpleGrantedAuthority::getAuthority).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                return new User(emailUser.get().getUsername(), emailUser.get().getPassword(), grantedAuthorities);

            } else {

                throw new UsernameNotFoundException("Invalid user");
            }

        }


}*/
