package com.duoc.backend;

import com.duoc.backend.Medication.Medication;
import com.duoc.backend.Medication.MedicationRepository;
import com.duoc.backend.Medication.MedicationService;
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
class MedicationServiceTest {

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private MedicationService medicationService;

    private Medication medication;

    @BeforeEach
    void setUp() {
        medication = new Medication();
        medication.setId(1L);
        medication.setName("Amoxicilina");
        medication.setCost(8500.0);
    }

    @Test
    void getAllMedications_debeRetornarListaDeMedications() {
        Medication medication2 = new Medication();
        medication2.setId(2L);
        medication2.setName("Ibuprofeno veterinario");
        medication2.setCost(6000.0);

        when(medicationRepository.findAll()).thenReturn(Arrays.asList(medication, medication2));

        List<Medication> result = medicationService.getAllMedications();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Amoxicilina", result.get(0).getName());
        verify(medicationRepository, times(1)).findAll();
    }

    @Test
    void getMedicationById_cuandoExiste_debeRetornarMedication() {
        when(medicationRepository.findById(1L)).thenReturn(Optional.of(medication));

        Medication result = medicationService.getMedicationById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Amoxicilina", result.getName());
        assertEquals(8500.0, result.getCost());
        verify(medicationRepository, times(1)).findById(1L);
    }

    @Test
    void getMedicationById_cuandoNoExiste_debeRetornarNull() {
        when(medicationRepository.findById(99L)).thenReturn(Optional.empty());

        Medication result = medicationService.getMedicationById(99L);

        assertNull(result);
        verify(medicationRepository, times(1)).findById(99L);
    }

    @Test
    void saveMedication_debeGuardarYRetornarMedication() {
        when(medicationRepository.save(medication)).thenReturn(medication);

        Medication result = medicationService.saveMedication(medication);

        assertNotNull(result);
        assertEquals("Amoxicilina", result.getName());
        assertEquals(8500.0, result.getCost());
        verify(medicationRepository, times(1)).save(medication);
    }

    @Test
    void deleteMedication_debeInvocarDeleteById() {
        doNothing().when(medicationRepository).deleteById(1L);

        medicationService.deleteMedication(1L);

        verify(medicationRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllMedications_cuandoNoHayMedications_debeRetornarListaVacia() {
        when(medicationRepository.findAll()).thenReturn(Arrays.asList());

        List<Medication> result = medicationService.getAllMedications();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void saveMedication_conNuevoCosto_debeActualizarCorrectamente() {
        Medication actualizado = new Medication();
        actualizado.setId(1L);
        actualizado.setName("Amoxicilina 500mg");
        actualizado.setCost(12000.0);

        when(medicationRepository.save(actualizado)).thenReturn(actualizado);

        Medication result = medicationService.saveMedication(actualizado);

        assertEquals("Amoxicilina 500mg", result.getName());
        assertEquals(12000.0, result.getCost());
    }
}
