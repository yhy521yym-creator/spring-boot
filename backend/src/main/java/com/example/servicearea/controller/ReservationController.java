package com.example.servicearea.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.common.ApiResponse;
import com.example.servicearea.entity.Reservation;
import com.example.servicearea.service.ReservationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/page")
    public ApiResponse<IPage<Reservation>> getPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        return ApiResponse.ok(reservationService.getPage(current, size, merchantId, status, userId));
    }

    @GetMapping
    public ApiResponse<List<Reservation>> getList(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        return ApiResponse.ok(reservationService.getList(merchantId, status, userId));
    }

    @GetMapping("/{id:\\d+}")
    public ApiResponse<Reservation> getById(@PathVariable Long id) {
        Reservation reservation = reservationService.getById(id);
        if (reservation == null) {
            return ApiResponse.fail(404, "预约不存在");
        }
        return ApiResponse.ok(reservation);
    }

    @PostMapping
    public ApiResponse<Boolean> save(@RequestBody @Validated ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setMerchantId(request.getMerchantId());
        reservation.setUserId(request.getUserId());
        reservation.setUserName(request.getUserName());
        reservation.setUserPhone(request.getUserPhone());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setDuration(request.getDuration());
        reservation.setQuantity(request.getQuantity());
        reservation.setRemark(request.getRemark());
        reservation.setOrderId(request.getOrderId());
        reservation.setStatus("PENDING");
        return ApiResponse.ok(reservationService.save(reservation));
    }

    @PutMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('reservation:edit') or hasAuthority('*')")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setMerchantId(request.getMerchantId());
        reservation.setUserId(request.getUserId());
        reservation.setUserName(request.getUserName());
        reservation.setUserPhone(request.getUserPhone());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setDuration(request.getDuration());
        reservation.setQuantity(request.getQuantity());
        reservation.setRemark(request.getRemark());
        return ApiResponse.ok(reservationService.update(reservation));
    }

    @PutMapping("/{id:\\d+}/status")
    @PreAuthorize("hasAuthority('reservation:edit') or hasAuthority('*')")
    public ApiResponse<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.ok(reservationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAuthority('reservation:delete') or hasAuthority('*')")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return ApiResponse.ok(reservationService.delete(id));
    }

    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        return ApiResponse.ok(reservationService.getStatistics());
    }

    @Data
    public static class ReservationRequest {
        private Long merchantId;
        private Long userId;
        private String userName;
        private String userPhone;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private Integer duration;
        private Integer quantity;
        private String remark;
        private Long orderId;
    }
}
