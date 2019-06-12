package ru.psv4.tempdatchiki.backend.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.psv4.tempdatchiki.backend.data.TdEntity;
import ru.psv4.tempdatchiki.backend.data.User;

import javax.persistence.EntityNotFoundException;

public interface CrudService<T extends TdEntity> {

	JpaRepository<T, String> getRepository();

	default T save(User currentUser, T entity) {
		return getRepository().saveAndFlush(entity);
	}

	default void delete(User currentUser, T entity) {
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		getRepository().deleteById(entity.getUid());
	}

	default void delete(User currentUser, String id) {
		delete(currentUser, load(id));
	}

	default long count() {
		return getRepository().count();
	}

	default T load(String id) {
		T entity = getRepository().findById(id).orElse(null);
		if (entity == null) {
			throw new EntityNotFoundException();
		}
		return entity;
	}
}
