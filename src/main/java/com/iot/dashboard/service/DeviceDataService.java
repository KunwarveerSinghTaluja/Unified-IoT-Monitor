package com.iot.dashboard.service;

import com.iot.dashboard.entity.DeviceData;
import com.iot.dashboard.repository.DeviceDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeviceDataService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    // ✅ Save sensor data
    public DeviceData saveDeviceData(DeviceData deviceData) {
        if (deviceData.getTimestamp() == null) {
            deviceData.setTimestamp(LocalDateTime.now());
        }
        return deviceDataRepository.save(deviceData);
    }

    // ✅ Get latest readings from all devices
    public List<DeviceData> getLatestReadings() {
        return deviceDataRepository.findLatestReadings();
    }

    // ✅ Get data for charts (last X hours)
    public Map<String, Object> getChartData(int hours) {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
        List<DeviceData> data = deviceDataRepository.findByTimestampAfter(cutoff);

        Map<String, Object> result = new HashMap<>();
        List<Double> temperatures = new ArrayList<>();
        List<Double> humidities = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Group data by hour
        Map<Integer, List<DeviceData>> hourlyData = new HashMap<>();

        for (DeviceData d : data) {
            int hour = d.getTimestamp().getHour();
            hourlyData.computeIfAbsent(hour, k -> new ArrayList<>()).add(d);
        }

        // Process each hour
        for (int hour = 0; hour < 24; hour++) {
            if (hourlyData.containsKey(hour)) {
                List<DeviceData> hourData = hourlyData.get(hour);

                // Calculate averages for this hour
                double avgTemp = hourData.stream()
                        .filter(d -> "temperature".equals(d.getDataType()))
                        .mapToDouble(DeviceData::getValue)
                        .average()
                        .orElse(0);

                double avgHumidity = hourData.stream()
                        .filter(d -> "humidity".equals(d.getDataType()))
                        .mapToDouble(DeviceData::getValue)
                        .average()
                        .orElse(0);

                temperatures.add(avgTemp);
                humidities.add(avgHumidity);
                labels.add(String.format("%02d:00", hour));
            } else {
                temperatures.add(0.0);
                humidities.add(0.0);
                labels.add(String.format("%02d:00", hour));
            }
        }

        result.put("temperatures", temperatures);
        result.put("humidities", humidities);
        result.put("labels", labels);

        return result;
    }
    public List<DeviceData> getAllDeviceData() {
        return deviceDataRepository.findAllByOrderByTimestampDesc();
    }

    // ✅ Get recent MQTT messages
    public List<DeviceData> getRecentMessages(int limit) {
        return deviceDataRepository.findTopNByOrderByTimestampDesc(limit);
    }

    // ✅ Get data by device ID
    public List<DeviceData> getDataByDeviceId(String deviceId) {
        return deviceDataRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
}