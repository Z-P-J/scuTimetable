package com.zpj.fragmentation.dialog.impl;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zpj.fragmentation.dialog.base.CenterDialogFragment;
import com.zpj.fragmentation.dialog.R;
import com.zpj.fragmentation.dialog.utils.DialogThemeUtils;
import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.utils.ScreenUtils;
import com.zpj.widget.checkbox.SmoothCheckBox;

import java.util.ArrayList;
import java.util.List;

public class CenterSelectDialogFragment<T> extends CenterDialogFragment {

    public interface OnMultiSelectListener<T> {
        void onSelect(List<Integer> selected, List<T> list);
    }

    public interface OnSingleSelectListener<T> {
        void onSelect(int position, T item);
    }

    public interface IconCallback<T> {
        void onGetIcon(ImageView icon, T item, int position);
    }

    public interface TitleCallback<T> {
        void onGetTitle(TextView titleView, T item, int position);
    }

    public interface SubtitleCallback<T> {
        void onGetSubtitle(TextView subtitleView, T item, int position);
    }

    private final List<Integer> selectedList = new ArrayList<>();

    private final List<T> list = new ArrayList<>();

    private boolean isMultiple = false;

    private OnSingleSelectListener<T> onSingleSelectListener;
    private OnMultiSelectListener<T> onMultiSelectListener;
    private IconCallback<T> iconCallback;
    private TitleCallback<T> titleCallback;
    private SubtitleCallback<T> subtitleCallback;

    String title;

    @Override
    protected int getContentLayoutId() {
        return R.layout._dialog_layout_center_impl_list;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setTextColor(DialogThemeUtils.getMajorTextColor(context));
        tvTitle.setText(title);
        LinearLayout buttons = findViewById(R.id.layout_buttons);
        if (isMultiple) {
            buttons.setVisibility(View.VISIBLE);
            TextView tvCancel = buttons.findViewById(R.id.tv_cancel);
            TextView tvOk = buttons.findViewById(R.id.tv_ok);
            tvCancel.setTextColor(DialogThemeUtils.getNegativeTextColor(context));
            tvOk.setTextColor(DialogThemeUtils.getPositiveTextColor(context));
            tvCancel.setOnClickListener(v -> onSelect());
            tvOk.setOnClickListener(v -> onSelect());
        } else {
            buttons.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        EasyRecyclerView<T> easyRecyclerView = new EasyRecyclerView<>(recyclerView);
        easyRecyclerView.setData(list)
                .setItemRes(R.layout._dialog_item_select)
                .setLayoutManager(new LinearLayoutManager(context))
                .onBindViewHolder((holder, list, position, ppayloads) -> {
                    ImageView iconView = holder.getView(R.id.icon_view);
                    TextView titleView = holder.getView(R.id.title_view);
                    titleView.setTextColor(DialogThemeUtils.getMajorTextColor(context));
                    TextView contentView = holder.getView(R.id.content_view);
                    contentView.setTextColor(DialogThemeUtils.getNormalTextColor(context));
                    final SmoothCheckBox checkBox = holder.getView(R.id.check_box);
                    checkBox.setChecked(selectedList.contains(position), true);
                    holder.setOnItemClickListener(v -> {
                        if (isMultiple) {
                            if (checkBox.isChecked()) {
                                unSelect(holder.getAdapterPosition());
                            } else {
                                onSelected(holder.getAdapterPosition());
                            }
                            easyRecyclerView.notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            if (!checkBox.isChecked()) {
                                easyRecyclerView.notifyItemChanged(selectedList.get(0));
                                selectedList.clear();
                                onSelected(holder.getAdapterPosition());
                                easyRecyclerView.notifyItemChanged(holder.getAdapterPosition());
                            }
                            onSelect();
                        }
                    });
                    if (iconCallback == null) {
                        iconView.setVisibility(View.GONE);
                    } else {
                        iconView.setVisibility(View.VISIBLE);
                        iconCallback.onGetIcon(iconView, list.get(position), position);
                    }
                    if (titleCallback == null) {
                        titleView.setVisibility(View.GONE);
                    } else {
                        titleView.setVisibility(View.VISIBLE);
                        titleCallback.onGetTitle(titleView, list.get(position), position);
                    }
                    if (subtitleCallback == null) {
                        contentView.setVisibility(View.GONE);
                    } else {
                        contentView.setVisibility(View.VISIBLE);
                        subtitleCallback.onGetSubtitle(contentView, list.get(position), position);
                    }
                })
                .build();
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
//        if (onSingleSelectListener != null) {
//            onSingleSelectListener.onSelect(selectedList.get(0), list.get(selectedList.get(0)));
//        } else if (onMultiSelectListener != null) {
//            onMultiSelectListener.onSelect(selectedList, list);
//        }
    }

    private void onSelect() {
        if (onSingleSelectListener != null) {
            onSingleSelectListener.onSelect(selectedList.get(0), list.get(selectedList.get(0)));
        } else if (onMultiSelectListener != null) {
            onMultiSelectListener.onSelect(selectedList, list);
        }
        dismiss();
    }

    public CenterSelectDialogFragment<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    public CenterSelectDialogFragment<T> setData(List<T> list) {
        this.list.addAll(list);
        return this;
    }

    public CenterSelectDialogFragment<T> setMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
        return this;
    }

    public CenterSelectDialogFragment<T> setSelected(int[] selected) {
        for (int position : selected) {
            onSelected(position);
        }
        if (selected.length > 1) {
            isMultiple = true;
        }
        return this;
    }

    public CenterSelectDialogFragment<T> setSelected(int selected) {
        onSelected(selected);
        return this;
    }

    public CenterSelectDialogFragment<T> setOnSingleSelectListener(OnSingleSelectListener<T> onSingleSelectListener) {
        this.onSingleSelectListener = onSingleSelectListener;
        return this;
    }

    public CenterSelectDialogFragment<T> setOnMultiSelectListener(OnMultiSelectListener<T> onMultiSelectListener) {
        this.onMultiSelectListener = onMultiSelectListener;
        return this;
    }

    public CenterSelectDialogFragment<T> setIconCallback(IconCallback<T> iconCallback) {
        this.iconCallback = iconCallback;
        return this;
    }

    public CenterSelectDialogFragment<T> setTitleCallback(TitleCallback<T> titleCallback) {
        this.titleCallback = titleCallback;
        return this;
    }

    public CenterSelectDialogFragment<T> setSubtitleCallback(SubtitleCallback<T> subtitleCallback) {
        this.subtitleCallback = subtitleCallback;
        return this;
    }


    private void onSelected(int position) {
        if (!selectedList.contains(position)) {
            selectedList.add(position);
        }
    }

    private void unSelect(int position) {
        selectedList.remove(Integer.valueOf(position));
    }

    @Override
    protected int getMaxWidth() {
        return (int) (ScreenUtils.getScreenWidth(context) * .9f);
    }


}