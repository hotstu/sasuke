package github.hotstu.sasukedemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import github.hotstu.sasuke.SasukeAdapter;
import github.hotstu.sasuke.SasukeView;

public class MainActivity extends AppCompatActivity {

    private SasukeView sasuke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sasuke = findViewById(R.id.sasuke);
        sasuke.setStickColumnHead(false);
        sasuke.setStickRowHead(false);
        sasuke.setAdapter(new MySasukeAdapter());
    }


    public void stickRow(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        sasuke.setStickRowHead(checked);
    }

    public void stickColumn(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        sasuke.setStickColumnHead(checked);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView t;
        public VH(TextView itemView) {
            super(itemView);
            t = itemView;
        }
    }

    class MySasukeAdapter extends SasukeAdapter {
        @Override
        public int getRowCount() {
            return 100;
        }

        @Override
        public int getColumnCount() {
            return 12;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int row, int column) {
            if (row == 0) {
                ((VH) holder).t.setBackgroundColor(Color.CYAN);
            }
            if (column == 0) {
                ((VH) holder).t.setBackgroundColor(Color.CYAN);
            }
            ((VH) holder).t.setText("" + row + "-" + column);
        }

        @Override
        public int getItemViewType(int row, int column) {
            return (row == 0|| column == 0 ? 1: 0);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setLayoutParams(new ViewGroup.LayoutParams(300, 200));
            tv.setBackgroundResource(R.drawable.sasuke_bg_item);
            return new VH(tv);
        }

    };
}
