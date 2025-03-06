package com.midas.crm.service;


import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import java.util.List;

public interface ClienteResidencialService {
    List<ClienteResidencial> listarTodos();
    ClienteResidencial obtenerPorId(Long id);
    ClienteResidencial guardar(ClienteResidencial cliente);
    ClienteResidencial actualizar(Long id, ClienteResidencial cliente);
    void eliminar(Long id);
    List<ClienteConUsuarioDTO> obtenerClientesConUsuario();

}
