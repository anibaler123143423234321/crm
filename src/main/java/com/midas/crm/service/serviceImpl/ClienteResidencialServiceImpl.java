package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.ClienteResidencial;
import com.midas.crm.repository.ClienteResidencialRepository;
import com.midas.crm.service.ClienteResidencialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClienteResidencialServiceImpl implements ClienteResidencialService {

    @Autowired
    private ClienteResidencialRepository clienteRepo;

    @Override
    public List<ClienteResidencial> listarTodos() {
        return clienteRepo.findAll();
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
        return clienteRepo.save(cliente);
    }


    @Override
    public ClienteResidencial actualizar(Long id, ClienteResidencial cliente) {
        if (!clienteRepo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado con id: " + id);
        }
        cliente.setId(id);  // aseguramos que actualice el existente
        return clienteRepo.save(cliente);
    }

    @Override
    public void eliminar(Long id) {
        if (!clienteRepo.existsById(id)) {
            throw new NoSuchElementException("Cliente no encontrado con id: " + id);
        }
        clienteRepo.deleteById(id);
    }
}
