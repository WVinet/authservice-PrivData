//package com.privdata.authservice.config;//package com.imaginepro.ticketera.auth.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");//obtiene el header desde la request HTTP
//
//        //Comprobamos si header no existe o si no empiza con Bearer , significa que no hay token JWT
//        //En ese caso dejamos pasar la request sin autenticar
//        if (authHeader == null || !authHeader.startsWith("Bearer ")){
//            filterChain.doFilter(request, response);
//            System.out.println("header no existe o no empieza con bearer");
//            return;
//        }
//        //extraemos el token sin la palabra bearer
//        String token = authHeader.substring(7);
//        System.out.println("extraemos token sin la palabra bearer");
//
//        //extraemos el username(email) del token
//        String email = jwtService.extractUsername(token);
//        System.out.println("extraemos username (email) del token");
//
//        //si el email no existe y todavia no hay un usuario autenticado
//        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){
//
//            //Cargamos el usuario desde la BDD usando el email
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//            //validamos que el usuario exista en la BDD y que el token no este expirado
//            if (jwtService.isTokenValid(token,userDetails)){
//                //Creamos un objeto Authentication que representara al usuario autenticado
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                //Agregamos informacion adicional de la request(IP, sessionId, etc), bien podria no ir
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                //Guardamos la autenticacion en el contexto de seguridad de Spring
//                //Desde aqui el usuario se considera autenticado por Spring
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//                System.out.println("isTokenValid");
//            }
//            System.out.println("El email exite y esta autenticado");
//        }
//        System.out.println("El email no existe , no hay usuario autenticado");
//        //Continuamos con el resto de filtros de Spring security
//        filterChain.doFilter(request, response);
//    }
//}
