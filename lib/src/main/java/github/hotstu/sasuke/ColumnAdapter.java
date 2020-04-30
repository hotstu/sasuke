package github.hotstu.sasuke;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public  class ColumnAdapter extends RecyclerView.Adapter {
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