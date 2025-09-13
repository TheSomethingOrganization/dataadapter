package io.kitsuri.dataadapter;

public interface ViewHolderTypeResolver<T> {

    ViewHolderType<T> resolveType(T data);

}
