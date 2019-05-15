package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.psv4.tempdatchiki.backend.data.Controller;
import ru.psv4.tempdatchiki.backend.data.Sensor;
import ru.psv4.tempdatchiki.backend.data.Subscription;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

	Page<Subscription> findBy(Pageable page);

	List<Subscription> findByController(Controller controller);
}
