package com.duoc.backend;

import com.duoc.backend.Care.Care;
import com.duoc.backend.Care.CareRepository;
import com.duoc.backend.Invoice.Invoice;
import com.duoc.backend.Invoice.InvoiceRepository;
import com.duoc.backend.Invoice.InvoiceService;
import com.duoc.backend.Medication.Medication;
import com.duoc.backend.Medication.MedicationRepository;

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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Mock
    private CareRepository careRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Invoice invoice;
    private Medication medication;
    private Care care;

    @BeforeEach
    void setUp() {
        medication = new Medication();
        medication.setId(1L);
        medication.setName("Amoxicilina");
        medication.setCost(8500.0);

        care = new Care();
        care.setId(1L);
        care.setName("Baño completo");
        care.setCost(15000.0);

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setPatientName("Firulais");
        invoice.setDate(LocalDate.of(2026, 5, 10));
        invoice.setTime(LocalTime.of(10, 30));
        invoice.setMedications(Arrays.asList(medication));
        invoice.setCares(Arrays.asList(care));
    }

    @Test
    void getAllInvoices_debeRetornarListaDeInvoices() {
        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setPatientName("Michi");

        when(invoiceRepository.findAll()).thenReturn(Arrays.asList(invoice, invoice2));

        Iterable<Invoice> result = invoiceService.getAllInvoices();

        assertNotNull(result);
        List<Invoice> lista = (List<Invoice>) result;
        assertEquals(2, lista.size());
        verify(invoiceRepository, times(1)).findAll();
    }

    @Test
    void getInvoiceById_cuandoExiste_debeRetornarInvoice() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        Invoice result = invoiceService.getInvoiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Firulais", result.getPatientName());
        verify(invoiceRepository, times(1)).findById(1L);
    }

    @Test
    void getInvoiceById_cuandoNoExiste_debeRetornarNull() {
        when(invoiceRepository.findById(99L)).thenReturn(Optional.empty());

        Invoice result = invoiceService.getInvoiceById(99L);

        assertNull(result);
        verify(invoiceRepository, times(1)).findById(99L);
    }

    @Test
    void saveInvoice_conDatosValidos_debeCalcularTotalYGuardar() {
        when(medicationRepository.findAllById(anyList())).thenReturn(Arrays.asList(medication));
        when(careRepository.findAllById(anyList())).thenReturn(Arrays.asList(care));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        Invoice result = invoiceService.saveInvoice(invoice);

        assertNotNull(result);
        assertEquals(23500.0, result.getTotalCost());
        verify(invoiceRepository, times(1)).save(invoice);
    }

    @Test
    void saveInvoice_conMedicamentoInexistente_debeLanzarExcepcion() {
        when(medicationRepository.findAllById(anyList())).thenReturn(Arrays.asList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.saveInvoice(invoice);
        });

        assertEquals("Algunos medicamentos no existen en la base de datos.", ex.getMessage());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void saveInvoice_conCareInexistente_debeLanzarExcepcion() {
        when(medicationRepository.findAllById(anyList())).thenReturn(Arrays.asList(medication));
        when(careRepository.findAllById(anyList())).thenReturn(Arrays.asList());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            invoiceService.saveInvoice(invoice);
        });

        assertEquals("Algunos servicios no existen en la base de datos.", ex.getMessage());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void saveInvoice_conVariosItemsValidos_debeCalcularTotalCorrectamente() {
        Medication med2 = new Medication();
        med2.setId(2L);
        med2.setName("Ibuprofeno");
        med2.setCost(6000.0);

        Care care2 = new Care();
        care2.setId(2L);
        care2.setName("Corte de uñas");
        care2.setCost(5000.0);

        invoice.setMedications(Arrays.asList(medication, med2));
        invoice.setCares(Arrays.asList(care, care2));

        when(medicationRepository.findAllById(anyList())).thenReturn(Arrays.asList(medication, med2));
        when(careRepository.findAllById(anyList())).thenReturn(Arrays.asList(care, care2));
        when(invoiceRepository.save(invoice)).thenReturn(invoice);

        Invoice result = invoiceService.saveInvoice(invoice);

        assertEquals(34500.0, result.getTotalCost());
    }

    @Test
    void deleteInvoice_debeInvocarDeleteById() {
        doNothing().when(invoiceRepository).deleteById(1L);

        invoiceService.deleteInvoice(1L);

        verify(invoiceRepository, times(1)).deleteById(1L);
    }
}
