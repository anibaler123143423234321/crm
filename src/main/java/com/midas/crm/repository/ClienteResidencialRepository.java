package com.midas.crm.repository;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteResidencialRepository extends JpaRepository<ClienteResidencial, Long> {

    @Query("SELECT new com.midas.crm.entity.ClienteConUsuarioDTO(u.dni, cr.nombresApellidos, cr.fechaCreacion, cr.movilContacto) " +
            "FROM ClienteResidencial cr JOIN cr.usuario u")
    List<ClienteConUsuarioDTO> obtenerClientesConUsuario();

    // Método para buscar por número móvil
    Optional<ClienteResidencial> findByMovilContacto(String movilContacto);
}
