package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.dto.ClienteDTO;
import com.alquiler.alquilerbicicletas.enumerados.TipoDocumentoIdentidad;
import com.alquiler.alquilerbicicletas.modelos.Cliente;
import com.alquiler.alquilerbicicletas.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente crearCliente(Cliente cliente) {
        // Verificamos si el cliente ya existe por email
        if (clienteRepository.findByEmail(cliente.getEmail()) != null) {
            throw new RuntimeException("El cliente con el correo electrónico " + cliente.getEmail() + " ya existe.");
        }

        // Guardamos el cliente
        return clienteRepository.save(cliente);
    }

    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

   public Cliente obtenerClientePorEmail(String email) {
       Cliente cliente = clienteRepository.findByEmail(email);
       if (cliente == null) {
           throw new RuntimeException("Cliente no encontrado con el correo electrónico: " + email);
       }
       return cliente;
   }

    public Cliente mapearDtoAEntidad(ClienteDTO dto) {
        return Cliente.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .fechaNacimiento(dto.getFechaNacimiento())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .tipoDocumento(dto.getTipoDocumento())
                .documentoIdentidad(dto.getDocumentoIdentidad())
                .fotoDocumentoUrl(dto.getFotoDocumentoUrl()) // si aplica
                .fechaRegistro(LocalDate.now()) // automático
                .build();
    }

    /* --- helpers --- */
    private void validarDocumento(TipoDocumentoIdentidad tipo, String numero) {
        if (tipo == null) throw new RuntimeException("El tipo de documento es obligatorio.");
        if (numero == null || numero.isBlank()) throw new RuntimeException("El número de documento es obligatorio.");

        String doc = normalizarDoc(numero);

        switch (tipo) {
            case DNI:
                if (!doc.matches("^[0-9]{7,8}[A-Za-z]$"))
                    throw new RuntimeException("DNI inválido (8 dígitos + letra).");
                break;
            case NIE:
                if (!doc.matches("^[XYZ][0-9]{7}[A-Za-z]$"))
                    throw new RuntimeException("NIE inválido (X/Y/Z + 7 dígitos + letra).");
                break;
            case PASAPORTE:
                if (!doc.matches("^[A-Za-z0-9]{5,20}$"))
                    throw new RuntimeException("Pasaporte inválido (5-20 caracteres alfanuméricos).");
                break;
            case OTRO:
                if (!doc.matches("^[A-Za-z0-9\\-_/\\.]{4,30}$"))
                    throw new RuntimeException("Documento inválido (4-30 caracteres).");
                break;
        }
    }

    private String normalizarDoc(String v) { return v == null ? null : v.trim().toUpperCase(); }
}
