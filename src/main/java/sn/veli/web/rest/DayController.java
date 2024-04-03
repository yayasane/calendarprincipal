package sn.veli.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import sn.veli.domain.Day;
import sn.veli.service.DayService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/services/calendar")
public class DayController {

    @Autowired
    private DayService dayService;

    @GetMapping("/dayfinder")
    public ResponseEntity<Day> getDayByDateString(@RequestParam String date) {
        System.out.println("On logjgjhjh");
        Day day = dayService.findDayByDateString(date);
        // Prepare data for historical microservice
        String searchDate = LocalDateTime.now().toString(); // Adjust format if needed
        String request = "Search date: " + date;
        String response = "Day of week: " + day.getDayOfWeek();

        RestClient restClient = RestClient.create("http://localhost:8081");
        restClient.post()
            .uri("/historique/save")
            .contentType(MediaType.APPLICATION_JSON)
            .body(new HashMap() {{
                put("searchDate", searchDate);
                put("request", request);
                put("response", response);
            }})
            .retrieve()
            .toBodilessEntity();

        return ResponseEntity.ok(day);
    }

}
