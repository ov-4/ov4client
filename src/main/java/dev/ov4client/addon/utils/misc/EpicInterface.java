package dev.ov4client.addon.utils.misc;

@FunctionalInterface
public interface EpicInterface<T, E> {
    E get(T t);
}
