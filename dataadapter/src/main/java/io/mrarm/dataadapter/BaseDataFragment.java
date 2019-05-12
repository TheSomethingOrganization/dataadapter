package io.mrarm.dataadapter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDataFragment<T> implements DataFragment<T> {

    private final List<Listener> listeners = new ArrayList<>();

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    protected void notifyItemRangeInserted(int index, int count) {
        for (Listener l : listeners)
            l.onItemRangeInserted(this, index, count);
    }

    protected void notifyItemRangeRemoved(int index, int count) {
        for (Listener l : listeners)
            l.onItemRangeRemoved(this, index, count);
    }

    protected void notifyItemRangeChanged(int index, int count) {
        for (Listener l : listeners)
            l.onItemRangeChanged(this, index, count);
    }

    protected void notifyItemRangeMoved(int index, int toIndex, int count) {
        for (Listener l : listeners)
            l.onItemRangeMoved(this, index, toIndex, count);
    }

}
