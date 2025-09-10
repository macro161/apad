package com.apad.weather.repository;

import com.apad.weather.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {
    
    /**
     * Find the most recent weather record for a specific location
     */
    Optional<Weather> findTopByLocationIgnoreCaseOrderByTimestampDesc(String location);
    
    /**
     * Find all weather records for a specific location
     */
    List<Weather> findByLocationIgnoreCaseOrderByTimestampDesc(String location);
    
    /**
     * Find weather records for a location within a date range
     */
    List<Weather> findByLocationIgnoreCaseAndTimestampBetweenOrderByTimestampDesc(
            String location, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find weather records within a date range
     */
    List<Weather> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find weather records by country
     */
    List<Weather> findByCountryIgnoreCaseOrderByTimestampDesc(String country);
    
    /**
     * Find weather records within a geographic area (bounding box)
     */
    @Query("SELECT w FROM Weather w WHERE " +
           "w.latitude BETWEEN :minLat AND :maxLat AND " +
           "w.longitude BETWEEN :minLon AND :maxLon " +
           "ORDER BY w.timestamp DESC")
    List<Weather> findByGeographicArea(
            @Param("minLat") Double minLatitude,
            @Param("maxLat") Double maxLatitude,
            @Param("minLon") Double minLongitude,
            @Param("maxLon") Double maxLongitude);
    
    /**
     * Find recent weather records (within last N hours)
     */
    @Query("SELECT w FROM Weather w WHERE w.timestamp >= :since ORDER BY w.timestamp DESC")
    List<Weather> findRecentWeather(@Param("since") LocalDateTime since);
    
    /**
     * Delete old weather records before a specific date
     */
    void deleteByTimestampBefore(LocalDateTime date);
    
    /**
     * Count weather records for a specific location
     */
    long countByLocationIgnoreCase(String location);
}
