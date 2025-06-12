package com.Hinga.farmMis.repository;

import com.Hinga.farmMis.Model.FinanceStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinanceStatisticsRepository extends JpaRepository<FinanceStatistics, Long> {
    List<FinanceStatistics> findByFarmerId(Long farmerId);
    List<FinanceStatistics> findByFarmerIdAndMonth(Long farmerId, String month);
    List<FinanceStatistics> findByFarmerIdAndYear(Long farmerId, String year);
    List<FinanceStatistics> findByFarmerIdAndQuarter(Long farmerId, String quarter);
}