package io.mrarm.dataadapter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.mrarm.dataadapter.example.R;

public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        RecyclerView rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        List<String> testList = new ArrayList<>();
        for (int i = 0; i < 100; i++)
            testList.add("Test " + i);

        DataAdapter adapter = new DataAdapter();
        adapter.setSource(new DataMerger()
                .add(new SingleItemData<>("Header", SimpleViewHolder.headerType))
                .add(new ListData<>(testList, SimpleViewHolder.type)));
        rv.setAdapter(adapter);
    }



    public static class SimpleViewHolder extends ViewHolder<String> {

        public static ViewHolderType<String> type = ViewHolderType.from((ctx, parent) -> {
            TextView textView = new TextView(ctx);
            return new SimpleViewHolder(textView);
        });

        public static ViewHolderType<String> headerType = ViewHolderType.from((ctx, parent) -> {
            TextView textView = new TextView(ctx);
            textView.setTypeface(null, Typeface.BOLD);
            return new SimpleViewHolder(textView);
        });

        public SimpleViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void bind(String type) {
            ((TextView) itemView).setText(type);
        }

    }

}
