package dev.wakandaacademy.produdoro.usuario.application.api;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/public/v1/usuario")
public interface UsuarioAPI {
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	UsuarioCriadoResponse postNovoUsuario(@RequestBody @Valid UsuarioNovoRequest usuarioNovo);
	
	@PostMapping(value = "/foco/{idUsuario}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    void mudaStatusParaFoco(@RequestHeader("Authorization") String token, @PathVariable UUID idUsuario);

	@GetMapping(value = "/{idUsuario}")
	@ResponseStatus(code = HttpStatus.OK)
	UsuarioCriadoResponse buscaUsuarioPorId(@PathVariable UUID idUsuario);

	@PatchMapping(value = "/pausa-curta/{idUsuario}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	void mudaStatusParaPausaCurta(@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable UUID idUsuario);

	@PatchMapping("/pausaLonga/{idUsuario}")
	@ResponseStatus(code=HttpStatus.NO_CONTENT)
	void mudaStatusParaPausaLonga(@RequestHeader(name = "Authorization", required = true)String token,@PathVariable UUID idUsuario);

}
