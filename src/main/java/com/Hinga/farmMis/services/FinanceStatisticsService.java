package com.Hinga.farmMis.services;

import com.Hinga.farmMis.Model.FinanceStatistics;
import com.Hinga.farmMis.repository.FinanceStatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinanceStatisticsService {
    private final FinanceStatisticsRepository financeStatisticsRepository;

    public FinanceStatisticsService(FinanceStatisticsRepository financeStatisticsRepository) {
        this.financeStatisticsRepository = financeStatisticsRepository;
    }

    public FinanceStatistics saveTransaction(FinanceStatistics statistics) {
        return financeStatisticsRepository.save(statistics);
    }

    public List<FinanceStatistics> getFarmerStatistics(Long farmerId) {
        return financeStatisticsRepository.findByFarmerId(farmerId);
    }

    public Map<String, Double> getMonthlyRevenue(Long farmerId) {
        List<FinanceStatistics> stats = financeStatisticsRepository.findByFarmerId(farmerId);
        return stats.stream()
                .collect(Collectors.groupingBy(
                        FinanceStatistics::getMonth,
                        Collectors.summingDouble(FinanceStatistics::getTotalAmount)
                ));
    }

    public Map<String, Double> getProductCategoryRevenue(Long farmerId) {
        List<FinanceStatistics> stats = financeStatisticsRepository.findByFarmerId(farmerId);
        return stats.stream()
                .collect(Collectors.groupingBy(
                        FinanceStatistics::getProductCategory,
                        Collectors.summingDouble(FinanceStatistics::getTotalAmount)
                ));
    }

    public Map<String, Integer> getProductQuantitySold(Long farmerId) {
        List<FinanceStatistics> stats = financeStatisticsRepository.findByFarmerId(farmerId);
        return stats.stream()
                .collect(Collectors.groupingBy(
                        FinanceStatistics::getProductCategory,
                        Collectors.summingInt(FinanceStatistics::getQuantitySold)
                ));
    }

    public Map<String, Double> getQuarterlyRevenue(Long farmerId) {
        List<FinanceStatistics> stats = financeStatisticsRepository.findByFarmerId(farmerId);
        return stats.stream()
                .collect(Collectors.groupingBy(
                        FinanceStatistics::getQuarter,
                        Collectors.summingDouble(FinanceStatistics::getTotalAmount)
                ));
    }

    public Map<String, Double> getTotalExpenses(Long farmerId) {
        List<FinanceStatistics> stats = financeStatisticsRepository.findByFarmerId(farmerId);
        // Placeholder: Assume expenses are 50% of revenue
        // Replace with actual expense logic (e.g., query an Expenses table or use an expenseAmount field)
        double totalExpenses = stats.stream()
                .filter(stat -> stat.getPaymentStatus().equals("PAID"))
                .mapToDouble(stat -> stat.getTotalAmount() * 0.0)
                .sum();
        Map<String, Double> result = new HashMap<>();
        result.put("totalExpenses", totalExpenses);
        return result;
    }
}