package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;

import java.util.List;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {

	Page<Sensor> findBy(Pageable page);

	List<Sensor> findByController(Controller controller);

	Optional<Sensor> findByControllerAndNameIgnoreCase(Controller controller, String name);
}
