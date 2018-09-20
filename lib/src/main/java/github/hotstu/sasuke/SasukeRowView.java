package github.hotstu.sasuke;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * @author hglf
 * @since 2018/9/19
 */
public class SasukeRowView extends LinearLayout {
    FrameLayout first_item;
    RecyclerView list_row;

    public SasukeRowView(Context context) {
        super(context);
        init();
    }

    public SasukeRowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SasukeRowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(getContext()).inflate(R.layout.sakuke_row_layout, this, true);
        first_item = findViewById(R.id.first_item);
        list_row = findViewById(R.id.list_row);
        list_row.setHasFixedSize(true);
        list_row.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    }


    public void setAdapter(ColumnAdapter adapter) {
        boolean stickColumnHeader = adapter.stickRowHeader;
        list_row.setAdapter(adapter);
        if(stickColumnHeader) {
            first_item.setVisibility(View.VISIBLE);
        } else {
            first_item.setVisibility(View.GONE);
        }
    }
    public ColumnAdapter getAdpater() {
        return ((ColumnAdapter) list_row.getAdapter());
    }


    public static class ColumnAdapter extends RecyclerView.Adapter {
        int row;
        SasukeAdapter delegate;
        boolean stickRowHeader = true;

        ColumnAdapter(SasukeAdapter delegate, int row, boolean stickRowHeader) {
            this.delegate = delegate;
            this.row = row;
            this.stickRowHeader = stickRowHeader;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return delegate.onCreateViewHolder(parent, viewType);
        }

        @Override
        public int getItemViewType(int position) {
            return delegate.getItemViewType(row, position);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int p = position + (stickRowHeader? 1: 0);
            delegate.onBindViewHolder(holder,row, p);
        }

        public void setRow(int row) {
            if(row != this.row) {
                this.row = row;
                notifyDataSetChanged();
            }

        }

        @Override
        public int getItemCount() {
            return delegate.getColumnCount() + (stickRowHeader? -1: 0);
        }
    }
}
