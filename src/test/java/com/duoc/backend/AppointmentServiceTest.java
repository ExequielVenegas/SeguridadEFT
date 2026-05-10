package com.duoc.backend;

import com.duoc.backend.Appointment.Appointment;
import com.duoc.backend.Appointment.AppointmentRepository;
import com.duoc.backend.Appointment.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDate(LocalDate.of(2026, 5, 10));
        appointment.setTime(LocalTime.of(10, 30));
        appointment.setReason("Control rutinario");
        appointment.setVeterinarian("Dr. Soto");
    }

    @Test
    void getAllAppointments_debeRetornarListaDeAppointments() {
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setDate(LocalDate.of(2026, 5, 11));
        appointment2.setTime(LocalTime.of(14, 0));
        appointment2.setReason("Vacunación");
        appointment2.setVeterinarian("Dra. Mora");

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment, appointment2));

        Iterable<Appointment> result = appointmentService.getAllAppointments();

        assertNotNull(result);
        List<Appointment> lista = (List<Appointment>) result;
        assertEquals(2, lista.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    void getAppointmentById_cuandoExiste_debeRetornarAppointment() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        Appointment result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Control rutinario", result.getReason());
        assertEquals("Dr. Soto", result.getVeterinarian());
        verify(appointmentRepository, times(1)).findById(1L);
    }

    @Test
    void getAppointmentById_cuandoNoExiste_debeRetornarNull() {
        when(appointmentRepository.findById(99L)).thenReturn(Optional.empty());

        Appointment result = appointmentService.getAppointmentById(99L);

        assertNull(result);
        verify(appointmentRepository, times(1)).findById(99L);
    }

    @Test
    void saveAppointment_debeGuardarYRetornarAppointment() {
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        Appointment result = appointmentService.saveAppointment(appointment);

        assertNotNull(result);
        assertEquals(LocalDate.of(2026, 5, 10), result.getDate());
        assertEquals(LocalTime.of(10, 30), result.getTime());
        assertEquals("Dr. Soto", result.getVeterinarian());
        verify(appointmentRepository, times(1)).save(appointment);
    }

    @Test
    void deleteAppointment_debeInvocarDeleteById() {
        doNothing().when(appointmentRepository).deleteById(1L);

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void saveAppointment_conNuevosDatos_debeRetornarAppointmentCorrecto() {
        Appointment nuevo = new Appointment();
        nuevo.setId(3L);
        nuevo.setDate(LocalDate.of(2026, 6, 1));
        nuevo.setTime(LocalTime.of(9, 0));
        nuevo.setReason("Cirugía menor");
        nuevo.setVeterinarian("Dr. Ramírez");

        when(appointmentRepository.save(nuevo)).thenReturn(nuevo);

        Appointment result = appointmentService.saveAppointment(nuevo);

        assertEquals("Cirugía menor", result.getReason());
        assertEquals("Dr. Ramírez", result.getVeterinarian());
        assertEquals(LocalDate.of(2026, 6, 1), result.getDate());
    }
}
