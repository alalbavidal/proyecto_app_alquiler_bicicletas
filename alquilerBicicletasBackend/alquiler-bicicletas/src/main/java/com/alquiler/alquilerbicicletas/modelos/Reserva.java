package com.alquiler.alquilerbicicletas.modelos;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.enumerados.TipoCobro;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reserva", schema = "alquiler_bicicletas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifa;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "extras_total", precision = 10, scale = 2)
    private BigDecimal extrasTotal;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(name = "contrato_url", length = 300)
    private String contratoUrl;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservaBicicleta> bicicletas = new ArrayList<>();

    // Añadir esta relación a la clase Reserva
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservaAccesorio> accesorios = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservaExtra> extras = new ArrayList<>();

    @Column(name = "cancel_token", unique = true)
    private String cancelToken;

    @Column(name = "pagado", nullable = false)
    private boolean pagado;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cobro", nullable = false)
    private TipoCobro tipoCobro;






    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.fechaCreacion = now;
        this.fechaActualizacion = now;
        if (this.estado == null) {
            this.estado = EstadoReserva.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }


    // Métodos de negocio
    public boolean puedeCancelarse() {
        return this.estado == EstadoReserva.PENDIENTE ||
                this.estado == EstadoReserva.CONFIRMADA;
    }

    public boolean puedeModificarse() {
        return this.estado == EstadoReserva.PENDIENTE;
    }

    public void confirmar() {
        if (this.estado == EstadoReserva.PENDIENTE) {
            this.estado = EstadoReserva.CONFIRMADA;
        } else {
            throw new IllegalStateException("No se puede confirmar una reserva que no está pendiente.");
        }
    }

    public void cancelar() {
        this.estado = EstadoReserva.CANCELADA; // Asegúrate de que `EstadoReserva.CANCELADA` exista
    }


    public void completar() {
        if (this.estado == EstadoReserva.CONFIRMADA) {
            this.estado = EstadoReserva.COMPLETADA;
        } else {
            throw new IllegalStateException("No se puede completar una reserva que no ha sido confirmada.");
        }



    }

    public void eliminarExtra(Extra extra) {
    }
}