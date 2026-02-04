package com.iot.dashboard.controller;

import com.iot.dashboard.entity.Device;
import com.iot.dashboard.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    // Get all devices - FIXED: uses getAllDevices() method
    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    // Get device status summary - FIXED: uses getStatusSummary() method
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getDeviceStatus() {
        return ResponseEntity.ok(deviceService.getStatusSummary());
    }

    // Add new device - FIXED: uses createDevice() method (not saveDevice)
    @PostMapping
    public ResponseEntity<Device> addDevice(@RequestBody Device device) {
        return ResponseEntity.ok(deviceService.createDevice(device));
    }

    // Get device by ID - FIXED: uses getDeviceById() method
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    // Update device - FIXED: uses updateDevice() method
    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        return ResponseEntity.ok(deviceService.updateDevice(id, device));
    }

    // Delete device - FIXED: uses deleteDevice() method
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().build();
    }

    // Additional endpoint: Update device status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Device> updateDeviceStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        return ResponseEntity.ok(deviceService.updateDeviceStatus(id, status));
    }

    // Additional endpoint: Get devices by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Device>> getDevicesByType(@PathVariable String type) {
        return ResponseEntity.ok(deviceService.getDevicesByType(type));
    }

    // Additional endpoint: Get online devices
    @GetMapping("/online")
    public ResponseEntity<List<Device>> getOnlineDevices() {
        return ResponseEntity.ok(deviceService.getOnlineDevices());
    }
}