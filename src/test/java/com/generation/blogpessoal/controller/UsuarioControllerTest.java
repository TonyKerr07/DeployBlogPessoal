package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest (webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance (TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", null, null));
		
	}
	
	@Test
	@DisplayName("Cadastrar um usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario> 
			(new Usuario(0L, "Antonio Kerr", "antonio.kerr@hotmail.com", "123456789", "-", null ));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", 
				HttpMethod.POST, corpoRequisicao, Usuario.class);
			
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
		
	}

	@Test
	@DisplayName("Verificar a duplicação do usuário")
	public void naoDeveDuplicarUsuario() {
		usuarioService.cadastrarUsuario(new Usuario(0L, "Antonio Carlos", "email2@hotmail.com", "123456789", null, null));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario (0L, "Antonio Carlos", "email2@hotmail.com", "123456", null, null));
	
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", 
				HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
		
	@Test
	@DisplayName("Atualizar usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario
				(new Usuario(0L, "Antonio Kerr", "email2@hotmail.com", "123456789", null, null));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
				"Antonio neto", "email2@hotmail.com", "123456789", null, null);
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
	
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", 
				HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os usuário")
	public void deveMostratTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, 
				"Carlos Kerr", "email3@email.com", "123456789", null, null));
		
		usuarioService.cadastrarUsuario(new Usuario(0L, 
				"Carlos Neto", "email4@email.com", "123456789", null, null));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", 
				HttpMethod.GET, null, String.class);
			
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
	}
}

