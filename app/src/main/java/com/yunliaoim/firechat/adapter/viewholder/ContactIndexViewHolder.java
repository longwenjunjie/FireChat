package com.yunliaoim.firechat.adapter.viewholder;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.yunliaoim.firechat.R;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 *
 */
public class ContactIndexViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.contact_index_name)
    TextView mTextView;

    public ContactIndexViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public TextView getTextView() {

        return mTextView;
    }
}
