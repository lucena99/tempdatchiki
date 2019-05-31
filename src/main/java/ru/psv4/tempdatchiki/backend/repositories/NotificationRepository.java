package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.psv4.tempdatchiki.backend.data.Notification;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Sensor;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

	@Query("SELECT e FROM Notification e WHERE e.recipient = :recipient AND e.sensor = :sensor AND e.createdDatetime = " +
			"(SELECT MAX(e1.createdDatetime) FROM Notification e1 WHERE e1.recipient = :recipient AND e1.sensor = :sensor)")
	public Optional<Notification> findByRecipientAndSensorLast(Recipient recipient, Sensor sensor);
}
