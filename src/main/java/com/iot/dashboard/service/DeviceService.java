package com.iot.dashboard.service;

import com.iot.dashboard.entity.Device;
import com.iot.dashboard.repository.DeviceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    // Add this initialization method
    @PostConstruct
    public void initTestData() {
        if (deviceRepository.count() == 0) {
            System.out.println("=== INITIALIZING TEST DEVICES ===");

            // Device 1: Temperature Sensor
            Device device1 = new Device();
            device1.setName("Temperature Sensor");
            device1.setDeviceId("TEMP-001");
            device1.setType("sensor");
            device1.setLocation("Room 101");
            device1.setStatus("online");
            device1.setIpAddress("192.168.1.101");
            device1.setLastValue(25.5);
            device1.setLastUnit("°C");
            device1.setLastSeen(LocalDateTime.now().minusMinutes(5));
            deviceRepository.save(device1);

            // Device 2: Humidity Sensor
            Device device2 = new Device();
            device2.setName("Humidity Sensor");
            device2.setDeviceId("HUM-002");
            device2.setType("sensor");
            device2.setLocation("Room 102");
            device2.setStatus("online");
            device2.setIpAddress("192.168.1.102");
            device2.setLastValue(65.0);
            device2.setLastUnit("%");
            device2.setLastSeen(LocalDateTime.now().minusMinutes(10));
            deviceRepository.save(device2);

            // Device 3: Smart Light
            Device device3 = new Device();
            device3.setName("Smart Light");
            device3.setDeviceId("LIGHT-003");
            device3.setType("actuator");
            device3.setLocation("Living Room");
            device3.setStatus("offline");
            device3.setIpAddress("192.168.1.103");
            device3.setLastValue(1.0);
            device3.setLastUnit("state");
            device3.setLastSeen(LocalDateTime.now().minusHours(2));
            deviceRepository.save(device3);

            // Device 4: Security Camera
            Device device4 = new Device();
            device4.setName("Security Camera");
            device4.setDeviceId("CAM-004");
            device4.setType("camera");
            device4.setLocation("Entrance");
            device4.setStatus("warning");
            device4.setIpAddress("192.168.1.104");
            device4.setLastValue(0.0);
            device4.setLastUnit("motion");
            device4.setLastSeen(LocalDateTime.now().minusMinutes(30));
            deviceRepository.save(device4);

            System.out.println("=== ADDED " + deviceRepository.count() + " TEST DEVICES ===");
        } else {
            System.out.println("=== FOUND " + deviceRepository.count() + " EXISTING DEVICES ===");
        }
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDeviceById(Long id) {
        Optional<Device> device = deviceRepository.findById(id);
        if (device.isPresent()) {
            return device.get();
        } else {
            throw new RuntimeException("Device not found with id: " + id);
        }
    }

    public Device createDevice(Device device) {
        if (device.getDeviceId() == null || device.getDeviceId().isEmpty()) {
            device.setDeviceId("DEV-" + System.currentTimeMillis());
        }
        if (device.getStatus() == null) {
            device.setStatus("offline");
        }
        device.setLastSeen(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    public Device updateDevice(Long id, Device deviceDetails) {
        Device device = getDeviceById(id);
        if (deviceDetails.getName() != null) {
            device.setName(deviceDetails.getName());
        }
        if (deviceDetails.getType() != null) {
            device.setType(deviceDetails.getType());
        }
        if (deviceDetails.getLocation() != null) {
            device.setLocation(deviceDetails.getLocation());
        }
        if (deviceDetails.getStatus() != null) {
            device.setStatus(deviceDetails.getStatus());
        }
        if (deviceDetails.getIpAddress() != null) {
            device.setIpAddress(deviceDetails.getIpAddress());
        }
        return deviceRepository.save(device);
    }

    public void deleteDevice(Long id) {
        Device device = getDeviceById(id);
        deviceRepository.delete(device);
    }

    public Map<String, Object> getStatusSummary() {
        List<Device> allDevices = getAllDevices();

        Map<String, Object> summary = new HashMap<>();

        int onlineCount = (int) allDevices.stream().filter(d -> "online".equals(d.getStatus())).count();
        int offlineCount = (int) allDevices.stream().filter(d -> "offline".equals(d.getStatus())).count();
        int warningCount = (int) allDevices.stream().filter(d -> "warning".equals(d.getStatus())).count();

        summary.put("totalDevices", allDevices.size());
        summary.put("onlineDevices", onlineCount);
        summary.put("offlineDevices", offlineCount);
        summary.put("warningDevices", warningCount);
        summary.put("activeAlerts", 0);

        int total = allDevices.size();
        double uptime = total > 0 ? (onlineCount * 100.0) / total : 0;
        summary.put("uptimePercentage", String.format("%.1f%%", uptime));

        return summary;
    }

    public Device updateDeviceStatus(Long id, String status) {
        Device device = getDeviceById(id);
        device.setStatus(status);
        device.setLastSeen(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    public List<Device> getDevicesByType(String type) {
        return deviceRepository.findByType(type);
    }

    public List<Device> getOnlineDevices() {
        return deviceRepository.findByStatus("online");
    }
}