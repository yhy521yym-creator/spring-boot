package com.example.servicearea.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.servicearea.entity.Reservation;
import java.util.List;
import java.util.Map;

public interface ReservationService {
    IPage<Reservation> getPage(int current, int size, Long merchantId, String status, Long userId);
    List<Reservation> getList(Long merchantId, String status, Long userId);
    Reservation getById(Long id);
    boolean save(Reservation reservation);
    boolean update(Reservation reservation);
    boolean delete(Long id);
    boolean updateStatus(Long id, String status);
    Map<String, Object> getStatistics();
}
