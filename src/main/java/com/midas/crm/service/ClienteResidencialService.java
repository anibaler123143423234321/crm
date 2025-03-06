package com.midas.crm.service;


import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import java.util.List;
import java.util.Optional;

public interface ClienteResidencialService {
    List<ClienteResidencial> listarTodos();
    ClienteResidencial obtenerPorId(Long id);
    ClienteResidencial guardar(ClienteResidencial cliente);
    ClienteResidencial actualizar(Long id, ClienteResidencial cliente);
    void eliminar(Long id);
    List<ClienteConUsuarioDTO> obtenerClientesConUsuario();
    // Nuevo método para buscar por móvil
    Optional<ClienteResidencial> buscarPorMovil(String movilContacto);
}
