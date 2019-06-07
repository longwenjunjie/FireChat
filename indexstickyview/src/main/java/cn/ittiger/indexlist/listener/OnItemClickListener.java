package cn.ittiger.indexlist.listener;

import android.view.View;

/**
 *
 */
public interface OnItemClickListener<T> {
    /**
     * @param childView     点击选中的子视图
     * @param position      点击视图的索引
     * @param item      点击视图的类型
     */
    void onItemClick(View childView, int position, T item);
}
