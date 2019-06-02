package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Recipient;

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

	@Query("SELECT e FROM Controller e WHERE NOT EXISTS (SELECT s FROM Subscription s WHERE s.recipient =:recipient AND " +
			"s.controller = e)")
	@EntityGraph(value = Controller.ENTITY_GRAPTH_FULL, type = EntityGraph.EntityGraphType.LOAD)
	Page<Controller> findAll(Recipient recipient, Pageable pageable);

	@EntityGraph(value = Controller.ENTITY_GRAPTH_FULL, type = EntityGraph.EntityGraphType.LOAD)
	@Query("SELECT e FROM Controller e WHERE NOT EXISTS (SELECT s FROM Subscription s WHERE s.recipient =:recipient AND " +
			"s.controller = e) AND LOWER(e.name) LIKE LOWER(CONCAT('%',:searchQuery,'%'))")
	Page<Controller> findByNameContainingIgnoreCase(Recipient recipient, String searchQuery, Pageable pageable);

	@Query("SELECT COUNT(e) FROM Controller e WHERE NOT EXISTS (SELECT s FROM Subscription s WHERE s.recipient =:recipient AND " +
			"s.controller = e) AND LOWER(e.name) LIKE LOWER(CONCAT('%',:searchQuery,'%'))")
	long countByNameContainingIgnoreCase(Recipient recipient, String searchQuery);

	@Query("SELECT COUNT(e) FROM Controller e WHERE NOT EXISTS (SELECT s FROM Subscription s WHERE s.recipient =:recipient AND " +
			"s.controller = e)")
	long count(Recipient recipient);

	long countByNameContainingIgnoreCase(String searchQuery);
}
