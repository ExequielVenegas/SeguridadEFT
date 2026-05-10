package com.duoc.backend;

import com.duoc.backend.Care.Care;
import com.duoc.backend.Care.CareRepository;
import com.duoc.backend.Care.CareService;
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
class CareServiceTest {

    @Mock
    private CareRepository careRepository;

    @InjectMocks
    private CareService careService;

    private Care care;

    @BeforeEach
    void setUp() {
        care = new Care();
        care.setId(1L);
        care.setName("Baño completo");
        care.setCost(15000.0);
    }

    @Test
    void getAllCares_debeRetornarListaDeCares() {
        Care care2 = new Care();
        care2.setId(2L);
        care2.setName("Corte de uñas");
        care2.setCost(5000.0);

        when(careRepository.findAll()).thenReturn(Arrays.asList(care, care2));

        List<Care> result = careService.getAllCares();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Baño completo", result.get(0).getName());
        assertEquals("Corte de uñas", result.get(1).getName());
        verify(careRepository, times(1)).findAll();
    }

    @Test
    void getCareById_cuandoExiste_debeRetornarCare() {
        when(careRepository.findById(1L)).thenReturn(Optional.of(care));

        Care result = careService.getCareById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Baño completo", result.getName());
        assertEquals(15000.0, result.getCost());
        verify(careRepository, times(1)).findById(1L);
    }

    @Test
    void getCareById_cuandoNoExiste_debeRetornarNull() {
        when(careRepository.findById(99L)).thenReturn(Optional.empty());

        Care result = careService.getCareById(99L);

        assertNull(result);
        verify(careRepository, times(1)).findById(99L);
    }

    @Test
    void saveCare_debeGuardarYRetornarCare() {
        when(careRepository.save(care)).thenReturn(care);

        Care result = careService.saveCare(care);

        assertNotNull(result);
        assertEquals("Baño completo", result.getName());
        assertEquals(15000.0, result.getCost());
        verify(careRepository, times(1)).save(care);
    }

    @Test
    void deleteCare_debeInvocarDeleteById() {
        doNothing().when(careRepository).deleteById(1L);

        careService.deleteCare(1L);

        verify(careRepository, times(1)).deleteById(1L);
    }

    @Test
    void saveCare_conCostoActualizado_debeReflejarNuevoCosto() {
        Care careActualizado = new Care();
        careActualizado.setId(1L);
        careActualizado.setName("Baño completo premium");
        careActualizado.setCost(25000.0);

        when(careRepository.save(careActualizado)).thenReturn(careActualizado);

        Care result = careService.saveCare(careActualizado);

        assertEquals("Baño completo premium", result.getName());
        assertEquals(25000.0, result.getCost());
    }

    @Test
    void getAllCares_cuandoNoHayCares_debeRetornarListaVacia() {
        when(careRepository.findAll()).thenReturn(Arrays.asList());

        List<Care> result = careService.getAllCares();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
