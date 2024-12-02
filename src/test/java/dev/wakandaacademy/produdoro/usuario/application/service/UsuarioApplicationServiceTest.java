package dev.wakandaacademy.produdoro.usuario.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import dev.wakandaacademy.produdoro.DataHelper;
import dev.wakandaacademy.produdoro.handler.APIException;
import dev.wakandaacademy.produdoro.usuario.application.repository.UsuarioRepository;
import dev.wakandaacademy.produdoro.usuario.domain.StatusUsuario;
import dev.wakandaacademy.produdoro.usuario.domain.Usuario;

@ExtendWith(MockitoExtension.class)
class UsuarioApplicationServiceTest {

	@InjectMocks
	UsuarioApplicationService usuarioApplicationService;

	@Mock
	UsuarioRepository usuarioRepository;

	@Test
	void mudaStatusParaFoco() {
		// cenario
		Usuario usuario = DataHelper.createUsuario();
		when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(usuario.getIdUsuario())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaFoco(usuario.getEmail(), usuario.getIdUsuario());
		assertEquals(StatusUsuario.FOCO, usuario.getStatus());
		verify(usuarioRepository, times(1)).buscaUsuarioPorEmail(usuario.getEmail());
	}

	@Test
	void deveMudarStatusParaPausaCurta_QuandoStatusEstiverDiferente() {

		Usuario usuario = DataHelper.createUsuario();
		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
		assertEquals(StatusUsuario.PAUSA_CURTA, usuario.getStatus());
		verify(usuarioRepository, times(1)).salva(usuario);
	}

	@Test
	void naoDeveMudarStatusParaPausaCurta_QuandoPassarIdUsuarioInvalido() {
		Usuario usuario = DataHelper.createUsuario();
		UUID idUsuario = UUID.fromString("bdc6b227-1fa5-47ad-930c-3d458afe2bc5");
		when(usuarioRepository.buscaUsuarioPorEmail(any())).thenReturn(usuario);
		APIException e = assertThrows(APIException.class,
				() -> usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), idUsuario));
		assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusException());
	}

	@Test
	void naoDeveMudarStatusParaPausaCurta_QuandoStatusEstiverEmPausaCurta() {
		Usuario usuario = DataHelper.createUsuario();
		when(usuarioRepository.buscaUsuarioPorEmail(usuario.getEmail())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaPausaCurta(usuario.getEmail(), usuario.getIdUsuario());
		APIException exception = assertThrows(APIException.class, usuario::verificaSeJaEstaEmPausaCurta);
		assertEquals("Usuário já está em Pausa Curta.", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusException());
	}

	@Test
	void deveMudaStatusParaPausaLonga() {
		Usuario usuario = DataHelper.createUsuario();

		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		when(usuarioRepository.buscaUsuarioPorId(any())).thenReturn(usuario);
		usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), usuario.getIdUsuario());
		assertEquals(StatusUsuario.PAUSA_LONGA, usuario.getStatus());
		verify(usuarioRepository, times(1)).salva(usuario);
	}

	@Test
	void naoDeveMudarParaPausaLonga() {
		Usuario usuario = DataHelper.createUsuario();

		UUID idUsuarioInvalido = UUID.fromString("e8d30618-a4b9-4f1d-be80-6e27b2d1c387");
		when(usuarioRepository.buscaUsuarioPorEmail(anyString())).thenReturn(usuario);
		APIException ex = assertThrows(APIException.class,
				() -> usuarioApplicationService.mudaStatusParaPausaLonga(usuario.getEmail(), idUsuarioInvalido));
		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusException());
	}
}