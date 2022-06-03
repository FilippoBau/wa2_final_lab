package it.polito.wa2.group22.ticketcatalogservice.security

import it.polito.wa2.group22.ticketcatalogservice.utils.stringToRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@EnableWebFluxSecurity
class SecurityConfig {

    @Autowired
    lateinit var jwtUtils: JWTUtils


    //TODO: aggiornare con annotazioni
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http.csrf().disable()
            .authorizeExchange {
                it
                    .pathMatchers("/admin/**").hasAuthority(stringToRole["ADMIN"].toString())
                    .pathMatchers("/orders/**").hasAuthority(stringToRole["USER"].toString())
                    .pathMatchers("/shop/**").hasAuthority(stringToRole["USER"].toString())
                    .pathMatchers("/tickets/**").permitAll()
                    .and().addFilterAt(JWTAuthorizationFilter(jwtUtils), SecurityWebFiltersOrder.FIRST)
            }.build()
    }
}