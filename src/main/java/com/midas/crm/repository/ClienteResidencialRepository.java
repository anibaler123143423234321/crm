package com.midas.crm.repository;

import com.midas.crm.entity.ClienteResidencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteResidencialRepository extends JpaRepository<ClienteResidencial, Long> {
    // Aquí se pueden definir consultas personalizadas si es necesario
}
