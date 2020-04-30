package github.hotstu.sasuke;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashSet;

/**
 * @author hglf
 * @since 2018/9/19
 */
public class SasukeView extends LinearLayout {

    private RecyclerView list_content;
    private SasukeRowView column_header;
    private boolean stickRowHead = true;
    private boolean stickColumnHead = true;
    private RowAdapter rowAdapter;

    public SasukeView(@NonNull Context context) {
        super(context);
        init();
    }

    public SasukeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SasukeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.sakuke_layout, this, true);
        list_content = findViewById(R.id.list_content);
        column_header = findViewById(R.id.column_header);
        list_content.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    /**
     * call before setAdapter !!
     */
    public void setStickColumnHead(boolean stickColumnHead) {
        if(this.stickColumnHead != stickColumnHead) {
            this.stickColumnHead = stickColumnHead;
            if (rowAdapter != null) {
                setAdapter(rowAdapter.delegate);
            }
        }
    }

    /**
     * call before setAdapter !!
     */
    public void setStickRowHead(boolean stickRowHead) {
        if(this.stickRowHead != stickRowHead) {
            this.stickRowHead = stickRowHead;
            if (rowAdapter != null) {
                setAdapter(rowAdapter.delegate);
            }
        }
    }

    public void setAdapter(SasukeAdapter adapter) {
        if (rowAdapter != null) {
            rowAdapter.destroy();
            rowAdapter = null;
        }
        if (adapter == null) {
            return;
        }
        rowAdapter = new RowAdapter(adapter, stickColumnHead, stickRowHead);
        list_content.setAdapter(rowAdapter);
        syncStickyHeaders(adapter);
    }

    private void syncStickyHeaders(SasukeAdapter adapter) {
        if (stickColumnHead) {
            column_header.setAdapter(new ColumnAdapter(adapter, 0, stickRowHead));
            column_header.setVisibility(View.VISIBLE);
            rowAdapter.initRow(column_header.list_row);
            if(stickRowHead) {
                column_header.first_item.setVisibility(View.VISIBLE);
                setUpFirstItemView(adapter);
            } else {
                column_header.first_item.setVisibility(View.GONE);
            }
        } else {
            column_header.setVisibility(View.GONE);
        }
    }

    private void setUpFirstItemView(SasukeAdapter delegate) {
        RecyclerView.ViewHolder viewHolder = delegate.onCreateViewHolder(column_header.first_item, delegate.getItemViewType(0, 0));
        delegate.onBindViewHolder(viewHolder, 0, 0);
        column_header.first_item.removeAllViews();
        column_header.first_item.addView(viewHolder.itemView);
    }


    static class RowVH extends RecyclerView.ViewHolder {
        SasukeRowView v;
        RecyclerView.ViewHolder firestVH;

        public RowVH(SasukeRowView itemView) {
            super(itemView);
            v = itemView;
        }
    }

    static class RowAdapter extends RecyclerView.Adapter<RowVH> {
        SasukeAdapter delegate;
        boolean stickColumnHeader = true;
        boolean stickRowHeader = true;
        int firstPos = -1;
        int firstOffset = -1;
        HashSet<RecyclerView> observerList;
        private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                firstPos = linearLayoutManager.findFirstVisibleItemPosition();
                View firstVisibleItem = linearLayoutManager.getChildAt(0);
                if (firstVisibleItem != null) {
                    firstOffset = linearLayoutManager.getDecoratedRight(firstVisibleItem);
                    for (RecyclerView rv : observerList) {
                        if (recyclerView != rv) {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.scrollToPositionWithOffset(firstPos + 1, firstOffset);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        };
        private OnTouchListener onTouchListener = new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN:
                        for (RecyclerView rv : observerList) {
                            if (rv != null) {
                                rv.stopScroll();
                            }
                        }
                }
                return false;
            }
        };

        RowAdapter(SasukeAdapter delegate, boolean stickColumnHeader, boolean stickRowHeader) {
            this.delegate = delegate;
            this.stickColumnHeader = stickColumnHeader;
            this.stickRowHeader = stickRowHeader;
            observerList = new HashSet<>();
        }

        @Override
        public RowVH onCreateViewHolder(ViewGroup parent, int viewType) {
            SasukeRowView v = new SasukeRowView(parent.getContext());
            initRow(v.list_row);
            return new RowVH(v);
        }


        private void initRow(RecyclerView recyclerView) {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null && firstPos > 0 && firstOffset > 0) {
                layoutManager.scrollToPositionWithOffset(firstPos + 1, firstOffset);
            }
            observerList.add(recyclerView);
            recyclerView.setOnTouchListener(onTouchListener);
            recyclerView.addOnScrollListener(onScrollListener);
        }

        public void destroy() {
            for (RecyclerView rv : observerList) {
                if (rv != null) {
                    rv.removeOnScrollListener(onScrollListener);
                    rv.setOnTouchListener(null);
                }
            }
            observerList.clear();
        }

        @Override
        public void onBindViewHolder(RowVH holder, int position) {
            int p = position + (stickColumnHeader ? 1 : 0);
            if (holder.v.getAdapter() == null) {
                holder.v.setAdapter(new ColumnAdapter(delegate, p, stickRowHeader));
            } else {
                holder.v.getAdapter().setRow(p);
            }
//            if (!observerList.contains(holder.v.list_row)) {
//                observerList.add(holder.v.list_row);
//            }
            if (stickRowHeader) {
                if (holder.firestVH == null) {
                    RecyclerView.ViewHolder viewHolder = delegate.onCreateViewHolder(holder.v.first_item, delegate.getItemViewType(p, 0));
                    delegate.onBindViewHolder(viewHolder, p, 0);
                    holder.v.first_item.removeAllViews();
                    holder.v.first_item.addView(viewHolder.itemView);
                    holder.firestVH = viewHolder;
                } else {
                    delegate.onBindViewHolder(holder.firestVH, p, 0);
                }
            }
        }

        @Override
        public int getItemCount() {
            return delegate.getRowCount() + (stickColumnHeader ? -1 : 0);
        }
    }


}
