package io.mrarm.dataadapter;

public interface DataFragment<T> {

    void bind();

    void unbind();


    int getItemCount();


    T getItem(int index);

    Object getContext(int index);

    ViewHolderType<T> getHolderTypeFor(int index);


    void addListener(Listener listener);

    void removeListener(Listener listener);

    interface Listener {
        void onItemRangeInserted(DataFragment fragment, int index, int count);
        void onItemRangeRemoved(DataFragment fragment, int index, int count);
        void onItemRangeChanged(DataFragment fragment, int index, int count);
        void onItemRangeMoved(DataFragment fragment, int index, int toIndex, int count);
    }

}
