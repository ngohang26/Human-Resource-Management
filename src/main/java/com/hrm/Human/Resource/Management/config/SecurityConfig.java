    package com.hrm.Human.Resource.Management.config;

    import com.hrm.Human.Resource.Management.jwt.JwtAuthenticationFilter;
    import com.hrm.Human.Resource.Management.jwt.JwtTokenProvider;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Lazy;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    public class SecurityConfig {
        private final UserDetailsService userDetailsService;

        public SecurityConfig(@Lazy UserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }
        @Autowired
        private JwtTokenProvider tokenProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .authorizeRequests()

                    .requestMatchers(HttpMethod.GET, "/employees/**").hasAnyAuthority("READ_ALL", "READ_SELF", "READ_EMPLOYEE")
                    .requestMatchers(HttpMethod.POST, "/api/register").hasAuthority("CREATE_USER")
                    .requestMatchers(HttpMethod.POST, "/api/login").permitAll()

                    .requestMatchers(HttpMethod.POST, "/positions/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/positions/getAllPositions").hasAuthority("READ_ALL")
                    .requestMatchers(HttpMethod.POST, "/departments/**").hasAuthority("READ_ALL")
                    .requestMatchers(HttpMethod.GET, "/departments/**").hasAnyAuthority("READ_ALL", "READ_SELF")
                    .requestMatchers(HttpMethod.POST, "/employees/**").hasAuthority("READ_ALL")
                    .requestMatchers(HttpMethod.PUT, "/employees/**").hasAuthority("READ_ALL")
                    .requestMatchers(HttpMethod.DELETE, "/employees/**").hasAuthority("READ_ALL")
                    .anyRequest().authenticated()
                    .and()
                    .csrf().disable();
            return http.build();
        }


        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }


//
//    @Configuration
//    public class SecurityConfig {
//        private final UserDetailsService userDetailsService;
//
//        public SecurityConfig(@Lazy UserDetailsService userDetailsService) {
//            this.userDetailsService = userDetailsService;
//        }
//
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//            http
//                    .authorizeRequests()
//                    .requestMatchers(HttpMethod.GET, "/employees/**").permitAll()
//                    .requestMatchers(HttpMethod.POST, "/api/register").permitAll()
//                    .requestMatchers(HttpMethod.POST, "/positions/addPosition").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/positions/**").hasAnyRole("HR", "ADMIN")
//                    .requestMatchers(HttpMethod.POST, "/employees/**").hasAnyRole("HR", "ADMIN")
//                    .requestMatchers(HttpMethod.PUT, "/employees/**").hasAnyRole("HR", "ADMIN")
//                    .requestMatchers(HttpMethod.DELETE, "/employees/**").hasAnyRole("HR", "ADMIN")
//                    .anyRequest().authenticated()
//                    .and()
//                    .csrf().disable();
//            return http.build();
//        }
//
//        @Bean
//        public PasswordEncoder passwordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//    }
