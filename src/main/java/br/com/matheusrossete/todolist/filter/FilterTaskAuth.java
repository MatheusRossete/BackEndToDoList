package br.com.matheusrossete.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.matheusrossete.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {


    @Autowired
    private IUserRepository usernameRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if(servletPath.startsWith("/tasks/")){ // se estiver na rota de tasks, executar
            // Pegar a autenticação
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim(); //retira o "basic" do inicio da Authorization, deixando apenas o código

            byte[] authDecoded = Base64.getDecoder().decode(authEncoded); //decodifica o codigo da authorization para byte

            var authString = new String(authDecoded); //transforma os bytes em String

            String[] credentials = authString.split(":"); // divide a String em dois vetores (antes e depois do :)
            String username = credentials[0]; //espaço antes do :
            String password = credentials[1]; // depois do :

            System.out.println("Authorization");
            System.out.println(username);
            System.out.println(password);

            // Validar usuário
            var user = this.usernameRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401); // não autorizado
            } else {
                // Validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()); // verifica se a senha inserida é igual a senha do usuario. tochararray é usado pois verify espera um array
                if(passwordVerify.verified) { //se a senha for valida
                    // Segue viagem
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response); //
                }else{
                    response.sendError(401); // se nao, "unauthorized"
                }
            }
        }else{
            filterChain.doFilter(request, response); //se nao, segue o jogo
        }
    }
}
