package com.alquiler.alquilerbicicletas.repositorios;

import com.alquiler.alquilerbicicletas.enumerados.TipoDocumentoIdentidad;
import com.alquiler.alquilerbicicletas.modelos.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Cliente findByEmail(String email);
    Cliente findByTipoDocumentoAndDocumentoIdentidad(TipoDocumentoIdentidad tipoDocumento, String documentoIdentidad);


    Cliente findByDocumentoIdentidad(String documentoIdentidad);

    List<Cliente> findByNombre(String nombre);

    List<Cliente> findByApellido(String apellido);

    List<Cliente> findByTelefono(String telefono);

    List<Cliente> findByFechaNacimiento(LocalDate fechaNacimiento);

    List<Cliente> findByFechaRegistro(LocalDate fechaRegistro);

}

