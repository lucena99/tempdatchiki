package ru.psv4.tempdatchiki.backend.data;

public final class EntityUtil {

	public static final String getName(Class<? extends TdEntity> type) {
		// All main entities have simple one word names, so this is sufficient. Metadata
		// could be added to the class if necessary.
		return type.getSimpleName();
	}
}
