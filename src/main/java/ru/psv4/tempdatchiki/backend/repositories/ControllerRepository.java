package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Controller;

import java.util.List;
import java.util.Optional;

public interface ControllerRepository extends JpaRepository<Controller, String> {

	@Override
	List<Controller> findAll();

	@Override
	@EntityGraph(value = Controller.ENTITY_GRAPTH_FULL, type = EntityGraph.EntityGraphType.LOAD)
	Page<Controller> findAll(Pageable pageable);

	@Override
	@EntityGraph(value = Controller.ENTITY_GRAPTH_FULL, type = EntityGraph.EntityGraphType.LOAD)
	Optional<Controller> findById(String id);

	@EntityGraph(value = Controller.ENTITY_GRAPTH_FULL, type = EntityGraph.EntityGraphType.LOAD)
	Page<Controller> findByNameContainingIgnoreCase(String searchQuery, Pageable pageable);

	long countByNameContainingIgnoreCase(String searchQuery);
}
