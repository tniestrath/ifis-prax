package com.analysetool.api;

import com.analysetool.repositories.UniqueUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(originPatterns = "*" , allowCredentials = "true")
@RestController
@RequestMapping("/uniqueusers")
public class UniqueUserController {

    @Autowired
    UniqueUserRepository uniqueUserRepo;

    // Endpoint, um die durchschnittliche Verweildauer aller Nutzer als String zurückzugeben
    @GetMapping("/average-time-spent")
    public String getAverageTimeSpent() {
        Double averageTimeSpent = uniqueUserRepo.getAverageTimeSpent();
        return averageTimeSpent != null ? String.format("%.2f", averageTimeSpent) : "Daten nicht verfügbar";
    }

    // Endpoint, um die durchschnittliche Verweildauer der Nutzer für den heutigen Tag als String zurückzugeben
    @GetMapping("/average-time-spent-today")
    public String getTodayAverageTimeSpent() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        Double todayAverageTimeSpent = uniqueUserRepo.getAverageTimeSpentBetweenDates(startOfDay, endOfDay);
        return todayAverageTimeSpent != null ? String.format("%.2f", todayAverageTimeSpent) : "Daten nicht verfügbar";
    }

    /**
     * Berechnet die durchschnittliche Verweildauer der Nutzer für einen spezifizierten Zeitraum.
     * Der Zeitraum wird durch 'daysBackFrom' und 'daysBackTo' relativ zum aktuellen Datum definiert.
     *
     * @param daysBackFrom Start des Zeitraums in Tagen vor heute (z.B. 7 für 7 Tage vor heute).
     * @param daysBackTo   Ende des Zeitraums in Tagen vor heute (z.B. 3 für 3 Tage vor heute).
     * @return Durchschnittliche Verweildauer als String oder 'Daten nicht verfügbar' bei fehlenden Daten.
     */
    @GetMapping("/average-time-spent-range")
    public String getAverageTimeSpentInRange(@RequestParam("daysBackFrom") int daysBackFrom, @RequestParam("daysBackTo") int daysBackTo) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(daysBackFrom).toLocalDate().atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now().minusDays(daysBackTo).toLocalDate().atTime(23, 59, 59);

        Double averageTimeSpent = uniqueUserRepo.getAverageTimeSpentBetweenDates(startDate, endDate);
        return averageTimeSpent != null ? String.format("%.2f", averageTimeSpent) : "Daten nicht verfügbar";
    }
    
}
