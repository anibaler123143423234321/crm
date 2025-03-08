package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.ClienteConUsuarioDTO;
import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.repository.ClienteResidencialRepository;
import com.midas.crm.service.ClienteResidencialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ClienteResidencialServiceImpl implements ClienteResidencialService {

    @Autowired
    private ClienteResidencialRepository clienteRepo;

    public ClienteResidencialServiceImpl(ClienteResidencialRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    @Override
    public List<ClienteResidencial> listarTodos() {
        return clienteRepo.findAll();
    }

    @Override
    public Page<ClienteConUsuarioDTO> obtenerClientesConUsuario(Pageable pageable) {
        return clienteRepo.obtenerClientesConUsuario(pageable);
    }


    @Override
    public Optional<ClienteResidencial> buscarPorMovil(String movil) {
        System.out.println("Buscando cliente con móvil: " + movil);
        Optional<ClienteResidencial> cliente = clienteRepo.findByMovilContacto(movil);
        cliente.ifPresentOrElse(
                c -> System.out.println("Cliente encontrado: " + c),
                () -> System.out.println("Cliente no encontrado")
        );
        return cliente;
    }

    @Override
    public ClienteResidencial obtenerPorId(Long id) {
        return clienteRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con id: " + id));
    }

    @Override
    public ClienteResidencial guardar(ClienteResidencial cliente) {
        if (cliente.getUsuario() == null) {
            throw new IllegalArgumentException("El cliente debe tener un usuario asociado");
        }
        cliente.setFechaCreacion(LocalDateTime.now());
        return clienteRepo.save(cliente);
    }

    @Override
    public ClienteResidencial actualizar(Long id, ClienteResidencial cliente) {
        if (!clienteRepo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado con id: " + id);
        }
        cliente.setId(id);  // Aseguramos que actualice el existente
        return clienteRepo.save(cliente);
    }

    @Override
    public void eliminar(Long id) {
        if (!clienteRepo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado con id: " + id);
        }
        clienteRepo.deleteById(id);
    }

    @Override
    public Page<ClienteConUsuarioDTO> obtenerClientesConUsuarioFiltrados(String dniAsesor, String nombreAsesor, String numeroMovil, LocalDate fecha, Pageable pageable) {
        return clienteRepo.obtenerClientesConUsuarioFiltrados(dniAsesor, nombreAsesor, numeroMovil, fecha, pageable);
    }
}

