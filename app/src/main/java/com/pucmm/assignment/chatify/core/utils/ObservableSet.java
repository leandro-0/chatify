package com.pucmm.assignment.chatify.core.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ObservableSet<E> implements Set<E> {
    private final Set<E> set = new HashSet<>();
    private SetChangeListener<E> listener;

    public void setChangeListener(SetChangeListener<E> listener) {
        this.listener = listener;
    }

    @Override
    public boolean add(E e) {
        boolean added = set.add(e);
        if (added && listener != null) {
            listener.onAdd(e);
        }
        return added;
    }

    @Override
    public boolean remove(Object o) {
        boolean removed = set.remove(o);
        if (removed && listener != null) {
            listener.onRemove((E) o);
        }
        return removed;
    }

    @Override
    public int size() { return set.size(); }

    @Override
    public boolean isEmpty() { return set.isEmpty(); }

    @Override
    public boolean contains(Object o) { return set.contains(o); }

    @Override
    public Iterator<E> iterator() { return set.iterator(); }

    @Override
    public Object[] toArray() { return set.toArray(); }

    @Override
    public <T> T[] toArray(T[] a) { return set.toArray(a); }

    @Override
    public boolean containsAll(Collection<?> c) { return set.containsAll(c); }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = set.addAll(c);
        if (modified && listener != null) {
            for (E e : c) {
                listener.onAdd(e);
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) { return set.retainAll(c); }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = set.removeAll(c);
        if (modified && listener != null) {
            for (Object o : c) {
                listener.onRemove((E) o);
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        set.clear();
    }

    public interface SetChangeListener<E> {
        void onAdd(E element);
        void onRemove(E element);
    }
}
