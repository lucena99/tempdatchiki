package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.psv4.tempdatchiki.backend.data.Message;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Setting;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

	public Optional<Message> findByName(String name);

	Page<Message> findByNameContainingIgnoreCase(String searchQuery, Pageable pageable);

	long countByNameContainingIgnoreCase(String searchQuery);

	@Query("SELECT e FROM Message e WHERE e.recipient = :recipient AND e.sensor = :sensor AND e.updatedDatetime = " +
			"(SELECT MAX(e1.updatedDatetime) FROM Message e1 WHERE e1.recipient = :recipient AND e1.sensor = :sensor)")
	public Optional<Message> findByRecipientAndSensorLast(Recipient recipient, Sensor sensor);
}
