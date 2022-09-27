package com.foundation.widget.utils.ui;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.foundation.widget.utils.ext.view.UIContextExtKt;

/**
 * 兼容Fragment的用于优化kotlin的getActivity()为activity
 */
public interface IUIFieldOpt {
    /**
     * Activity为自己
     * Fragment为宿主
     * 非null请使用requireActivity，{@link UIContextExtKt#getRequireActivity}
     */
    @Nullable
    FragmentActivity getActivity();
}
