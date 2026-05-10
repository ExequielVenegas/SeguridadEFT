package com.duoc.backend;

import com.duoc.backend.Patient.Patient;
import com.duoc.backend.Patient.PatientRepository;
import com.duoc.backend.Patient.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setName("Firulais");
        patient.setSpecies("Perro");
        patient.setBreed("Labrador");
        patient.setAge(3);
        patient.setOwner("Juan Pérez");
    }

    @Test
    void getAllPatients_debeRetornarListaDePatients() {
        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setName("Michi");
        patient2.setSpecies("Gato");

        when(patientRepository.findAll()).thenReturn(Arrays.asList(patient, patient2));

        Iterable<Patient> result = patientService.getAllPatients();

        assertNotNull(result);
        List<Patient> lista = (List<Patient>) result;
        assertEquals(2, lista.size());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void getPatientById_cuandoExiste_debeRetornarPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Firulais", result.getName());
        assertEquals("Perro", result.getSpecies());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_cuandoNoExiste_debeRetornarNull() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        Patient result = patientService.getPatientById(99L);

        assertNull(result);
        verify(patientRepository, times(1)).findById(99L);
    }

    @Test
    void savePatient_debeGuardarYRetornarPatient() {
        when(patientRepository.save(patient)).thenReturn(patient);

        Patient result = patientService.savePatient(patient);

        assertNotNull(result);
        assertEquals("Firulais", result.getName());
        assertEquals("Labrador", result.getBreed());
        assertEquals(3, result.getAge());
        assertEquals("Juan Pérez", result.getOwner());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void deletePatient_debeInvocarDeleteById() {
        doNothing().when(patientRepository).deleteById(1L);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).deleteById(1L);
    }

    @Test
    void savePatient_conDatosNuevos_debeRetornarPatientActualizado() {
        Patient nuevo = new Patient();
        nuevo.setId(2L);
        nuevo.setName("Rex");
        nuevo.setSpecies("Perro");
        nuevo.setBreed("Pastor Alemán");
        nuevo.setAge(5);
        nuevo.setOwner("María López");

        when(patientRepository.save(nuevo)).thenReturn(nuevo);

        Patient result = patientService.savePatient(nuevo);

        assertEquals("Rex", result.getName());
        assertEquals("Pastor Alemán", result.getBreed());
        assertEquals(5, result.getAge());
    }
}
