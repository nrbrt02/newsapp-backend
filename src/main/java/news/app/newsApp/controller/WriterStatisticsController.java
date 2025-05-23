package news.app.newsApp.controller;

import news.app.newsApp.dto.WriterStatisticsDto;
import news.app.newsApp.service.WriterStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/writer/statistics")
@PreAuthorize("hasAnyRole('ADMIN', 'WRITER')")
public class WriterStatisticsController {

    @Autowired
    private WriterStatisticsService writerStatisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<WriterStatisticsDto> getWriterDashboard() {
        return ResponseEntity.ok(writerStatisticsService.getWriterDashboard());
    }

    @GetMapping("/articles/performance")
    public ResponseEntity<Map<String, Object>> getArticlesPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(writerStatisticsService.getArticlesPerformance(startDate, endDate));
    }

    @GetMapping("/articles/engagement")
    public ResponseEntity<Map<String, Object>> getArticlesEngagement(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(writerStatisticsService.getArticlesEngagement(startDate, endDate));
    }

    @GetMapping("/categories/performance")
    public ResponseEntity<Map<String, Object>> getCategoriesPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(writerStatisticsService.getCategoriesPerformance(startDate, endDate));
    }

    @GetMapping("/readers/insights")
    public ResponseEntity<Map<String, Object>> getReadersInsights(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(writerStatisticsService.getReadersInsights(startDate, endDate));
    }
} 