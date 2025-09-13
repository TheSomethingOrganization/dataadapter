package io.kitsuri.dataadapter;

public interface ListViewHolderTypeResolver<T> {

    ViewHolderType<T> resolveType(int index, T data);

}
