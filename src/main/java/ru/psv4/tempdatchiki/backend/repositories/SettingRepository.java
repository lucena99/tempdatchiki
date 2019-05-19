package ru.psv4.tempdatchiki.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.psv4.tempdatchiki.backend.data.Recipient;
import ru.psv4.tempdatchiki.backend.data.Setting;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {

	public Optional<Setting> findByName(String name);

	Page<Setting> findByNameContainingIgnoreCase(String searchQuery, Pageable pageable);

	long countByNameContainingIgnoreCase(String searchQuery);
}
