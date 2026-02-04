package com.iot.dashboard.controller;

import com.iot.dashboard.entity.DeviceData;
import com.iot.dashboard.service.DeviceDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private DeviceDataService deviceDataService;

    // Get latest sensor readings
    @GetMapping("/current")
    public ResponseEntity<List<DeviceData>> getCurrentData() {
        return ResponseEntity.ok(deviceDataService.getLatestReadings());
    }

    // Get chart data
    @GetMapping("/charts/{hours}")
    public ResponseEntity<Map<String, Object>> getChartData(@PathVariable int hours) {
        return ResponseEntity.ok(deviceDataService.getChartData(hours));
    }

    // Get recent messages
    @GetMapping("/messages")
    public ResponseEntity<List<DeviceData>> getRecentMessages() {
        return ResponseEntity.ok(deviceDataService.getRecentMessages(50));
    }
    @GetMapping("/device-data")
    public ResponseEntity<List<DeviceData>> getAllDeviceData() {
        return ResponseEntity.ok(deviceDataService.getAllDeviceData());
    }

    // Save data from IoT devices
    @PostMapping
    public ResponseEntity<DeviceData> saveDeviceData(@RequestBody DeviceData deviceData) {
        return ResponseEntity.ok(deviceDataService.saveDeviceData(deviceData));
    }
}