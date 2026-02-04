package com.iot.dashboard.service;

import com.iot.dashboard.entity.DeviceData;
import com.iot.dashboard.repository.DeviceDataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DeviceDataService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    // Add this initialization method
    @PostConstruct
    public void initTestData() {
        if (deviceDataRepository.count() == 0) {
            System.out.println("=== INITIALIZING TEST DEVICE DATA ===");

            LocalDateTime now = LocalDateTime.now();

            // Create test data for the last 24 hours
            for (int i = 0; i < 24; i++) {
                LocalDateTime timestamp = now.minusHours(i);

                // Temperature data for TEMP-001
                DeviceData tempData = new DeviceData();
                tempData.setDeviceId("TEMP-001");
                tempData.setDataType("temperature");
                tempData.setValue(20.0 + Math.random() * 10); // Random temp between 20-30°C
                tempData.setUnit("°C");
                tempData.setTopic("/sensors/temperature");
                tempData.setTimestamp(timestamp.minusMinutes((int)(Math.random() * 30)));
                deviceDataRepository.save(tempData);

                // Humidity data for HUM-002
                DeviceData humData = new DeviceData();
                humData.setDeviceId("HUM-002");
                humData.setDataType("humidity");
                humData.setValue(50.0 + Math.random() * 30); // Random humidity between 50-80%
                humData.setUnit("%");
                humData.setTopic("/sensors/humidity");
                humData.setTimestamp(timestamp.minusMinutes((int)(Math.random() * 30)));
                deviceDataRepository.save(humData);

                // Light state data (every 4 hours)
                if (i % 4 == 0) {
                    DeviceData lightData = new DeviceData();
                    lightData.setDeviceId("LIGHT-003");
                    lightData.setDataType("state");
                    lightData.setValue(Math.random() > 0.5 ? 1.0 : 0.0); // Random on/off
                    lightData.setUnit("on/off");
                    lightData.setTopic("/actuators/light");
                    lightData.setTimestamp(timestamp);
                    deviceDataRepository.save(lightData);
                }

                // Motion data (random)
                if (Math.random() > 0.7) {
                    DeviceData motionData = new DeviceData();
                    motionData.setDeviceId("CAM-004");
                    motionData.setDataType("motion");
                    motionData.setValue(1.0);
                    motionData.setUnit("detected");
                    motionData.setTopic("/security/motion");
                    motionData.setTimestamp(timestamp.minusMinutes((int)(Math.random() * 50)));
                    deviceDataRepository.save(motionData);
                }
            }

            System.out.println("=== ADDED " + deviceDataRepository.count() + " TEST DATA RECORDS ===");
        } else {
            System.out.println("=== FOUND " + deviceDataRepository.count() + " EXISTING DATA RECORDS ===");
        }
    }


    public DeviceData saveDeviceData(DeviceData deviceData) {
        if (deviceData.getTimestamp() == null) {
            deviceData.setTimestamp(LocalDateTime.now());
        }
        return deviceDataRepository.save(deviceData);
    }


    public List<DeviceData> getLatestReadings() {
        return deviceDataRepository.findLatestReadings();
    }


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


    public List<DeviceData> getRecentMessages(int limit) {
        return deviceDataRepository.findTopNByOrderByTimestampDesc(limit);
    }

    // Add this overloaded method for your controller
    public List<DeviceData> getRecentMessages() {
        return getRecentMessages(10); // Default to 10 messages
    }


    public List<DeviceData> getDataByDeviceId(String deviceId) {
        return deviceDataRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }
}