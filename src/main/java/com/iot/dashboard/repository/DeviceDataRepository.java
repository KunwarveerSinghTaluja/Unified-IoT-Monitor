package com.iot.dashboard.repository;

import com.iot.dashboard.entity.DeviceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeviceDataRepository extends JpaRepository<DeviceData, Long> {

    // Find latest reading for each device
    @Query("SELECT d FROM DeviceData d WHERE d.timestamp IN " +
            "(SELECT MAX(d2.timestamp) FROM DeviceData d2 GROUP BY d2.deviceId)")
    List<DeviceData> findLatestReadings();

    // Find data after timestamp
    List<DeviceData> findByTimestampAfter(LocalDateTime timestamp);

    // Get recent N messages
    List<DeviceData> findTopNByOrderByTimestampDesc(int n);

    // Find by device ID
    List<DeviceData> findByDeviceIdOrderByTimestampDesc(String deviceId);
    List<DeviceData> findAllByOrderByTimestampDesc();

    // Find by data type
    List<DeviceData> findByDataTypeOrderByTimestampDesc(String dataType);
}