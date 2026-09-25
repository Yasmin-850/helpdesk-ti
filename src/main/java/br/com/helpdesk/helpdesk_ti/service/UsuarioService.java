package br.com.helpdesk.helpdesk_ti.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.helpdesk.helpdesk_ti.dto.UsuarioResumo;
import br.com.helpdesk.helpdesk_ti.model.Usuario;
import br.com.helpdesk.helpdesk_ti.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private static final String PERFIL_ADMIN = "ADMIN";
    private static final String PERFIL_SOLICITANTE = "SOLICITANTE";

    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return null;
        }

        return repository.findByEmailIgnoreCase(email.trim())
                .filter(usuario -> encoder.matches(senha, usuario.getSenha()))
                .orElse(null);
    }

    public Usuario criarSeNaoExiste(
            String nome,
            String email,
            String senha,
            String perfil,
            String setor) {

        return repository.findByEmailIgnoreCase(email)
                .orElseGet(() -> repository.save(
                        new Usuario(
                                nome,
                                email,
                                encoder.encode(senha),
                                perfil,
                                setor
                        )
                ));
    }

    public List<UsuarioResumo> listar() {
        return repository.findAll()
                .stream()
                .map(this::resumo)
                .toList();
    }

    public UsuarioResumo buscarResumo(Long id) {
        return resumo(buscarUsuarioPorId(id));
    }

    public UsuarioResumo editar(
            Long id,
            String nome,
            String email,
            String perfil,
            String setor,
            Long usuarioLogadoId) {

        Usuario usuario = buscarUsuarioPorId(id);

        validarNomeEEmail(nome, email);

        String perfilNormalizado = normalizarPerfil(perfil, usuario.getPerfil());

        validarPerfil(perfilNormalizado);
        validarEmailDisponivel(email, id);
        validarAlteracaoAdministrador(usuario, perfilNormalizado, usuarioLogadoId);

        usuario.setNome(nome.trim());
        usuario.setEmail(normalizarEmail(email));
        usuario.setPerfil(perfilNormalizado);
        usuario.setSetor(normalizarSetor(setor));

        return resumo(repository.save(usuario));
    }

    public UsuarioResumo criar(
            String nome,
            String email,
            String senha,
            String perfil,
            String setor) {

        validarDadosNovoUsuario(nome, email, senha);

        String perfilNormalizado =
                normalizarPerfil(perfil, PERFIL_SOLICITANTE);

        validarPerfil(perfilNormalizado);
        validarEmailDisponivel(email, null);

        Usuario usuario = new Usuario(
                nome.trim(),
                normalizarEmail(email),
                encoder.encode(senha),
                perfilNormalizado,
                normalizarSetor(setor)
        );

        return resumo(repository.save(usuario));
    }

    public void excluir(Long id, Long usuarioLogadoId) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Usuário inválido"
            );
        }

        if (id.equals(usuarioLogadoId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode excluir o usuário que está conectado"
            );
        }

        Usuario usuario = buscarUsuarioPorId(id);

        if (PERFIL_ADMIN.equals(usuario.getPerfil())
                && repository.countByPerfil(PERFIL_ADMIN) <= 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível excluir o último administrador do sistema"
            );
        }

        repository.delete(usuario);
    }

    public UsuarioResumo buscarPorEmail(String email) {
        Usuario usuario = repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));

        return resumo(usuario);
    }

    public UsuarioResumo editarMeuPerfil(
            Long id,
            String nome,
            String email,
            String setor) {

        Usuario usuario = buscarUsuarioPorId(id);

        validarNomeEEmail(nome, email);
        validarEmailDisponivel(email, id);

        usuario.setNome(nome.trim());
        usuario.setEmail(normalizarEmail(email));
        usuario.setSetor(normalizarSetor(setor));

        return resumo(repository.save(usuario));
    }

    public void alterarSenha(Long id, String atual, String nova) {
        Usuario usuario = buscarUsuarioPorId(id);

        if (atual == null || !encoder.matches(atual, usuario.getSenha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Senha atual incorreta"
            );
        }

        if (nova == null || nova.length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A nova senha deve ter pelo menos 6 caracteres"
            );
        }

        usuario.setSenha(encoder.encode(nova));
        repository.save(usuario);
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado"
                ));
    }

    private UsuarioResumo resumo(Usuario usuario) {
        return new UsuarioResumo(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getSetor()
        );
    }

    private void validarNomeEEmail(String nome, String email) {
        if (nome == null || nome.isBlank()
                || email == null || email.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Preencha nome e e-mail"
            );
        }
    }

    private void validarDadosNovoUsuario(
            String nome,
            String email,
            String senha) {

        if (nome == null || nome.isBlank()
                || email == null || email.isBlank()
                || senha == null || senha.length() < 6) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Preencha nome, e-mail e uma senha com pelo menos 6 caracteres"
            );
        }
    }

    private String normalizarPerfil(String perfil, String perfilPadrao) {
        return perfil == null
                ? perfilPadrao
                : perfil.trim().toUpperCase();
    }

    private void validarPerfil(String perfil) {
        if (!PERFIL_ADMIN.equals(perfil)
                && !PERFIL_SOLICITANTE.equals(perfil)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Perfil inválido"
            );
        }
    }

    private void validarEmailDisponivel(String email, Long usuarioId) {
        repository.findByEmailIgnoreCase(email.trim())
                .ifPresent(usuarioExistente -> {

                    boolean mesmoUsuario =
                            usuarioId != null
                                    && usuarioExistente.getId().equals(usuarioId);

                    if (!mesmoUsuario) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Já existe um usuário com este e-mail"
                        );
                    }
                });
    }

    private void validarAlteracaoAdministrador(
            Usuario usuario,
            String novoPerfil,
            Long usuarioLogadoId) {

        if (PERFIL_ADMIN.equals(usuario.getPerfil())
                && !PERFIL_ADMIN.equals(novoPerfil)
                && repository.countByPerfil(PERFIL_ADMIN) <= 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível remover o perfil do último administrador"
            );
        }

        if (usuario.getId().equals(usuarioLogadoId)
                && !PERFIL_ADMIN.equals(novoPerfil)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode remover seu próprio perfil de administrador enquanto está conectado"
            );
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizarSetor(String setor) {
        return setor == null ? "" : setor.trim();
    }
}
