package ru.psv4.tempdatchiki.model;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Regset<E extends IReg> implements Serializable, Iterable<E> {
    private static final long serialVersionUID = 1L;

    private HashSet<E> set;
    private Set<E> deleted;

    public Regset() {
        this.set = new HashSet<E>();
    }
    public Regset(Collection<E> list) {
        this.set = new HashSet<E>(list);
    }

    public class RSIterator implements Iterator<E> {
        private Iterator<E> setIterator;
        private E last;

        private RSIterator() {
            setIterator = set.iterator();
        }

        public boolean hasNext() {
            return setIterator.hasNext();
        }

        public E next() {
            last = setIterator.next();
            return last;
        }

        public void remove() {
            remember(last);
            setIterator.remove();
        }
        
    }

    /**
     * Помещает удаляемый элемент в специальный список.
     * @param element Удаляемый элемент.
     */
    protected void remember(E element) {
        if (deleted == null) deleted = new HashSet<E>();
        deleted.add(element);
    }

    public void delete(E element) {
        if (set.contains(element)) {
            remember(element);
            set.remove(element);
        }
    }

    /**
     * Сначала выполняется поиск элемента среди удалённых. Если удаётся найти,
     * вызывается метод для замены значений replaceValue. После этого элемент
     * восстанавливается из удалённых и добавляется в основной список.</br>
     * Если среди удалённых эквивалентный элемент не обнаружен, происходит
     * непосредственное добавление в основной список.
     * @param reg Элемент для добавления
     */
    public void add(E reg) {
        if (deleted != null) {
            Iterator<E> iterator = deleted.iterator();
            while (iterator.hasNext()) {
                E dr = iterator.next();
                if (((IReg<E>) dr).equivalent(reg)) {
                    replaceValue(dr, reg);
                    set.add(dr);
                    iterator.remove();
                    return;
                }
            }
        }
        set.add(reg);
    }

    /**
     * Вызывается из процедуры добавления элемента, в случае, когда эквивалентный элемент обнаружен
     * среди помеченных на удаление объектов и необходимо вернуть его оттуда в основной набор заменив
     * при этом значение на значение из добавляемого элемента.
     * @param found Элемент, обнаруженный среди помеченных на удаление.
     * @param item Добавляемый элемент.
     */
    abstract protected void replaceValue(E found, E item);

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public E get(int index) {
		return null;
	}

    public boolean hasDeleted() {
        return deleted != null && !deleted.isEmpty();
    }

    public Set<E> getDeleted() {
        return deleted;
    }

    public void clearDeleted() {
        if (deleted != null) deleted.clear();
    }

    /**
     * Помечает все элементы как удалённые
     */
    public void clear() {
        for (E element: set) {
            remember(element);
        }
        set.clear();
    }

    /**
     * Помечает удовлетворяющие условию элементы, как удалённые
     */
    public void clearIf(Predicate<E> cond) {
    	List<E> deleting = new ArrayList<E>();
		for (E reg : this) {
			if (cond.test(reg)) {
				deleting.add(reg);
			}
		}
		if (!deleting.isEmpty()) {
			for (E element : deleting) {
				delete(element);
			}
		}
    }

    public Iterator<E> iterator() {
        return new RSIterator();
    }

    public <T extends IReg> T[] toArray(T[] a) {
        return set.toArray(a);
    }

    public void save(EntityManager em) {
        if (hasDeleted()) {
            for (E reg: getDeleted()) {
                em.remove(em.merge(reg));
            }
        }
        if (!isEmpty()) {
            for (E reg: this) {
                em.merge(reg);
            }
        }
    }
}