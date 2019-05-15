package io.mrarm.dataadapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public final class DataBindingViewHolder<T, CT> extends ViewHolder<T, CT> {

    private final ViewDataBinding binding;
    private final DataBindingViewHolderType type;

    DataBindingViewHolder(@NonNull ViewDataBinding binding,
                          @NonNull DataBindingViewHolderType type) {
        super(binding.getRoot());
        this.binding = binding;
        this.type = type;
    }

    public Context getContext() {
        return itemView.getContext();
    }

    @Override
    public void bind(T value, CT context) {
        if (type.getValueVarId() != -1)
            binding.setVariable(type.getValueVarId(), value);
        if (type.getContextVarId() != -1)
            binding.setVariable(type.getContextVarId(), context);
        if (type.getBindCallback() != null) {
            //noinspection unchecked
            type.getBindCallback().onBind(this, binding, value, context);
        }
        binding.executePendingBindings();
    }



}
