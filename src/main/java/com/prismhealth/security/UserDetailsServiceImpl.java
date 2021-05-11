package com.prismhealth.security;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.prismhealth.Models.UserRoles;
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
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository userRepository;
    @Autowired
    private UserRolesRepo roleRepo;

    public UserDetailsServiceImpl() {
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {

        Optional<com.prismhealth.Models.User> phoneUser = Optional.ofNullable(userRepository.findOneByPhone(phone));
        if (phoneUser.isPresent()) {
            List<SimpleGrantedAuthority> grantedAuthorities = roleRepo.findAllByUserId(phoneUser.get().getPhone()).stream()
                    .map(UserRoles::getRole).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            return new User(phoneUser.get().getPhone(), phoneUser.get().getPassword(), grantedAuthorities);

        } else {

            throw new UsernameNotFoundException("Invalid user");
        }

    }

}
