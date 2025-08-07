package com.admindashboard;

import com.admindashboard.driververification.*;
import com.admindashboard.earnings.*;
import com.admindashboard.enums.*;
import com.admindashboard.exception.EntityNotFoundException;
import com.admindashboard.usermanagement.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarningServiceTest {

    @Mock
    private EarningRepository earningRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private EarningService earningService;

    private Driver driver;
    private User user;
    private Earning earning;
    private EarningDTO earningDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("Driver");
        user.setLastName("User");
        user.setEmail("driver@example.com");

        driver = new Driver();
        driver.setId(10L);
        driver.setUser(user); // Link driver to user

        earning = new Earning();
        earning.setId(1L);
        earning.setDriver(driver);
        earning.setAmount(BigDecimal.valueOf(50.00));
        earning.setTransactionDate(LocalDateTime.now());
        earning.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        earning.setPaymentStatus(PaymentStatus.PAID);

        earningDTO = new EarningDTO();
        earningDTO.setDriverId(10L);
        earningDTO.setDriverName("Driver User");
        earningDTO.setAmount(BigDecimal.valueOf(75.50));
        earningDTO.setTransactionDate(LocalDateTime.now());
        earningDTO.setPaymentMethod(PaymentMethod.WALLET);
        earningDTO.setPaymentStatus(PaymentStatus.PENDING);
    }

    @Test
    void testCreateEarningSuccess() {
        when(driverRepository.findById(anyLong())).thenReturn(Optional.of(driver));
        when(earningRepository.save(any(Earning.class))).thenReturn(earning);

        EarningDTO createdEarning = earningService.createEarning(earningDTO);

        assertNotNull(createdEarning);
        assertEquals(earning.getAmount(), createdEarning.getAmount());
        assertEquals(earning.getDriver().getId(), createdEarning.getDriverId());
        verify(earningRepository, times(1)).save(any(Earning.class));
    }

    @Test
    void testCreateEarningDriverNotFound() {
        when(driverRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> earningService.createEarning(earningDTO));
        verify(earningRepository, never()).save(any(Earning.class));
    }

    @Test
    void testGetAllEarningsSuccess() {
        when(earningRepository.findAll()).thenReturn(Arrays.asList(earning));

        List<EarningDTO> earnings = earningService.getAllEarnings();

        assertFalse(earnings.isEmpty());
        assertEquals(1, earnings.size());
        assertEquals(earning.getAmount(), earnings.get(0).getAmount());
        verify(earningRepository, times(1)).findAll();
    }

    @Test
    void testGetEarningByIdSuccess() {
        when(earningRepository.findById(1L)).thenReturn(Optional.of(earning));

        EarningDTO foundEarning = earningService.getEarningById(1L);

        assertNotNull(foundEarning);
        assertEquals(earning.getId(), foundEarning.getId());
        verify(earningRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEarningByIdNotFound() {
        when(earningRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> earningService.getEarningById(99L));
        verify(earningRepository, times(1)).findById(99L);
    }

    @Test
    void testGetEarningsByDriverSuccess() {
        when(driverRepository.existsById(10L)).thenReturn(true);
        when(earningRepository.findByDriverId(10L)).thenReturn(Arrays.asList(earning));

        List<EarningDTO> earnings = earningService.getEarningsByDriver(10L);

        assertFalse(earnings.isEmpty());
        assertEquals(1, earnings.size());
        assertEquals(earning.getDriver().getId(), earnings.get(0).getDriverId());
        verify(earningRepository, times(1)).findByDriverId(10L);
    }

    @Test
    void testGetEarningsByDriverNotFound() {
        when(driverRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> earningService.getEarningsByDriver(99L));
        verify(earningRepository, never()).findByDriverId(anyLong());
    }

    @Test
    void testGetEarningsByDateRangeSuccess() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        when(earningRepository.findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(earning));

        List<EarningDTO> earnings = earningService.getEarningsByDateRange(start, end);

        assertFalse(earnings.isEmpty());
        assertEquals(1, earnings.size());
        verify(earningRepository, times(1)).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetEarningsByDateRangeInvalidDates() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () -> earningService.getEarningsByDateRange(start, end));
        verify(earningRepository, never()).findByDateRange(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testGetEarningsByStatusSuccess() {
        when(earningRepository.findByPaymentStatus(PaymentStatus.PAID)).thenReturn(Arrays.asList(earning));

        List<EarningDTO> earnings = earningService.getEarningsByStatus(PaymentStatus.PAID);

        assertFalse(earnings.isEmpty());
        assertEquals(1, earnings.size());
        assertEquals(PaymentStatus.PAID, earnings.get(0).getPaymentStatus());
        verify(earningRepository, times(1)).findByPaymentStatus(PaymentStatus.PAID);
    }
}
