package IntegracionBackFront.backfront.Controller.Auth;

import IntegracionBackFront.backfront.Models.DTO.Users.UserDTO;
import IntegracionBackFront.backfront.Services.Auth.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/login")
    private ResponseEntity<String> login(@Valid @RequestBody UserDTO data, HttpServletResponse response){
        //1. Verificar que los datos no esten vacíos
        if (data.getCorreo() == null || data.getCorreo().isBlank() || data.getCorreo().isEmpty() ||
        data.getContrasena() == null || data.getContrasena().isBlank() || data.getContrasena().isEmpty()){
            return ResponseEntity.status(401).body("Error: credenciales incompletas");
        }
        //2. Enviar los datos al metodo login contenido en el service
        if (service.Login(data.getCorreo(), data.getContrasena())){
            return ResponseEntity.ok("Inicio de sesión exitoso");
        }
        return ResponseEntity.status(401).body("Credenciales incorrectas");
    }
}
