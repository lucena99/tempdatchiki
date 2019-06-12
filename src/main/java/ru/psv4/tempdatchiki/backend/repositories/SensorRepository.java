package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {

	Page<Sensor> findBy(Pageable page);

	List<Sensor> findByController(Controller controller);

	Optional<Sensor> findByControllerAndNameIgnoreCase(Controller controller, String name);

	@Query("SELECT coalesce(MAX(e.num),0) + 1 FROM Sensor e WHERE e.controller = :controller")
	public int getNextNum(Controller controller);
}
