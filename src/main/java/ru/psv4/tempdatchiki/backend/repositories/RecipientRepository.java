package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.psv4.tempdatchiki.backend.data.Recipient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, String> {

	@Override
	List<Recipient> findAll();

	@Override
	@EntityGraph(value = Recipient.ENTITY_GRAPTH_FULL, type = EntityGraphType.LOAD)
    Page<Recipient> findAll(Pageable pageable);

	@Override
	@EntityGraph(value = Recipient.ENTITY_GRAPTH_FULL, type = EntityGraphType.LOAD)
	Optional<Recipient> findById(String id);

	@EntityGraph(value = Recipient.ENTITY_GRAPTH_FULL, type = EntityGraphType.LOAD)
	Page<Recipient> findByNameContainingIgnoreCase(String searchQuery, Pageable pageable);

	long countByNameContainingIgnoreCase(String searchQuery);

	@EntityGraph(value = Recipient.ENTITY_GRAPTH_FULL, type = EntityGraphType.LOAD)
	public List<Recipient> findAllByFcmToken(String fcmToken);
}
