package ru.psv4.tempdatchiki.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.psv4.tempdatchiki.backend.data.TdEntity;

import java.util.Optional;

public interface FilterableCrudService<T extends TdEntity> extends CrudService<T> {

	Page<T> findAnyMatching(Optional<String> filter, Pageable pageable);

	long countAnyMatching(Optional<String> filter);

}
