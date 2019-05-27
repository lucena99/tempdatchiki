package ru.psv4.tempdatchiki.crud;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import ru.psv4.tempdatchiki.HasLogger;
import ru.psv4.tempdatchiki.backend.data.TdEntity;
import ru.psv4.tempdatchiki.backend.service.CrudService;
import ru.psv4.tempdatchiki.backend.service.UserFriendlyDataException;
import ru.psv4.tempdatchiki.security.CurrentUser;
import ru.psv4.tempdatchiki.ui.views.HasNotifications;
import ru.psv4.tempdatchiki.utils.messages.CrudErrorMessage;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.util.function.Consumer;

public class CrudEntityPresenter<E extends TdEntity> implements HasLogger {

	private final CrudService<E> crudService;

	private final CurrentUser currentUser;

	private final HasNotifications view;

	public CrudEntityPresenter(CrudService<E> crudService, CurrentUser currentUser, HasNotifications view) {
		this.crudService = crudService;
		this.currentUser = currentUser;
		this.view = view;
	}

	public void delete(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
		if (executeOperation(() -> crudService.delete(currentUser.getUser(), entity))) {
			onSuccess.accept(entity);
		} else {
			onFail.accept(entity);
		}
	}

	public void save(E entity, Consumer<E> onSuccess, Consumer<E> onFail) {
		if (executeOperation(() -> saveEntity(entity))) {
			onSuccess.accept(entity);
		} else {
			onFail.accept(entity);
		}
	}

	private boolean executeOperation(Runnable operation) {
		try {
			operation.run();
			return true;
		} catch (UserFriendlyDataException e) {
			// Commit failed because of application-level data constraints
			consumeError(e, e.getMessage(), true);
		} catch (DataIntegrityViolationException e) {
			// Commit failed because of validation errors
			consumeError(
					e, CrudErrorMessage.OPERATION_PREVENTED_BY_REFERENCES, true);
		} catch (OptimisticLockingFailureException e) {
			consumeError(e, CrudErrorMessage.CONCURRENT_UPDATE, true);
		} catch (EntityNotFoundException e) {
			consumeError(e, CrudErrorMessage.ENTITY_NOT_FOUND, false);
		} catch (ConstraintViolationException e) {
			consumeError(e, CrudErrorMessage.REQUIRED_FIELDS_MISSING, false);
		}
		return false;
	}

	private void consumeError(Exception e, String message, boolean isPersistent) {
		getLogger().debug(message, e);
		view.showNotification(message, isPersistent);
	}

	private void saveEntity(E entity) {
		crudService.save(currentUser.getUser(), entity);
	}

	public boolean loadEntity(String id, Consumer<E> onSuccess) {
		return executeOperation(() -> onSuccess.accept(crudService.load(id)));
	}
}