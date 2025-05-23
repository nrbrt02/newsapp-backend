package news.app.newsApp.controller;

import news.app.newsApp.dto.StatisticsDto;
import news.app.newsApp.service.AdminStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    @Autowired
    private AdminStatisticsService adminStatisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<StatisticsDto> getDashboardStatistics() {
        return ResponseEntity.ok(adminStatisticsService.getDashboardStatistics());
    }

    @GetMapping("/articles/overview")
    public ResponseEntity<Map<String, Object>> getArticlesOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(adminStatisticsService.getArticlesOverview(startDate, endDate));
    }

    @GetMapping("/users/overview")
    public ResponseEntity<Map<String, Object>> getUsersOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(adminStatisticsService.getUsersOverview(startDate, endDate));
    }

    @GetMapping("/categories/performance")
    public ResponseEntity<Map<String, Object>> getCategoriesPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(adminStatisticsService.getCategoriesPerformance(startDate, endDate));
    }

    @GetMapping("/writers/performance")
    public ResponseEntity<Map<String, Object>> getWritersPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(adminStatisticsService.getWritersPerformance(startDate, endDate));
    }

    @GetMapping("/engagement")
    public ResponseEntity<Map<String, Object>> getEngagementMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(adminStatisticsService.getEngagementMetrics(startDate, endDate));
    }
} 