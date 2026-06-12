package com.example.servicearea.security;

import com.example.servicearea.entity.SysUser;
import com.example.servicearea.service.PermissionService;
import com.example.servicearea.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final PermissionService permissionService;

    public CustomUserDetailsService(UserService userService, PermissionService permissionService) {
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("用户不存在");

        Set<String> permissions = permissionService.getPermissionsByRole(user.getRole());

        // 权限 + 角色都做成 authority，方便后续扩展
        var authorities = permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // SUPER_ADMIN 的 * 作为一个特殊 authority：后续可在鉴权时识别
        if (permissions.contains("*")) {
            authorities.add(new SimpleGrantedAuthority("*"));
        }

        return new UserPrincipal(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), authorities);
    }
}

