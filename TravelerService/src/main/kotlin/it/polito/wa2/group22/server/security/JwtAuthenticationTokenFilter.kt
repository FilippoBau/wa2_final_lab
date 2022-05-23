package it.polito.wa2.group22.server.security

import org.apache.tomcat.util.http.parser.Authorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

//TODO: COMPLETARE

@Component
class JwtAuthenticationTokenFilter: OncePerRequestFilter() {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    /*@Value("\${jwt.header}")
    lateinit var headerPrefix: String

    @Value("\${jwt.headerStart}")
    lateinit var headerStart: String*/


    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (authHeader != null && authHeader.startsWith("Bearer")){
            val token = authHeader.substring("Bearer ".length)
            if (jwtUtils.validateJwt(token)) {
                val userDetails = jwtUtils.getDetailsJwt(token)
                val authenticationToken =
                    UsernamePasswordAuthenticationToken(userDetails!!.username, null, userDetails.roles)
                println(userDetails.roles)
                SecurityContextHolder.getContext().setAuthentication(authenticationToken)
                filterChain.doFilter(request, response)
            }else{
                response.setHeader("error", "Invalid access")
                response.sendError(HttpStatus.FORBIDDEN.value())
            }
        }
    }
}