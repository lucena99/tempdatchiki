package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.psv4.tempdatchiki.backend.data.Temp;

public interface TempRepository extends JpaRepository<Temp, String> {

	Page<Temp> findBy(Pageable page);
}
