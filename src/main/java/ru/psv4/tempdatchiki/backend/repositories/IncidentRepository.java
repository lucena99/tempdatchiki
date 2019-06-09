package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.psv4.tempdatchiki.backend.data.Incident;
import ru.psv4.tempdatchiki.backend.data.Sensor;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, String> {

	Page<Incident> findBy(Pageable page);

	@Query("SELECT e FROM Incident e WHERE e.sensor = :sensor AND e.createdDatetime = " +
			"(SELECT MAX(e1.createdDatetime) FROM Incident e1 WHERE e1.sensor = :sensor)")
	public Optional<Incident> findBySensorLast(Sensor sensor);

	public List<Incident> getListBySensorOrderByCreatedDatetime(Sensor sensor);

	@Query("SELECT e FROM Incident e WHERE LOWER(e.sensor.name) LIKE LOWER(CONCAT('%',:searchQuery,'%')) OR " +
			"LOWER(e.sensor.controller.name) LIKE LOWER(CONCAT('%',:searchQuery,'%'))")
	Page<Incident> findByNameContainingIgnoreCase(String searchQuery, Pageable pageable);

	@Query("SELECT COUNT(e) FROM Incident e WHERE LOWER(e.sensor.name) LIKE LOWER(CONCAT('%',:searchQuery,'%')) OR " +
			"LOWER(e.sensor.controller.name) LIKE LOWER(CONCAT('%',:searchQuery,'%'))")
	long countByNameContainingIgnoreCase(String searchQuery);
}
