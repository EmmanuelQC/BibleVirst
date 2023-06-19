package com.emmanuel.biblevirst;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VerseAdapter extends RecyclerView.Adapter<VerseAdapter.VerseHolder> {

    public static class VerseHolder extends RecyclerView.ViewHolder {
        public LinearLayout containerView;
        private Button deleteBtn;
        public TextView textView;

        public VerseHolder(View view) {
            super(view);
            this.containerView = view.findViewById(R.id.verse_row);
            this.textView = view.findViewById(R.id.verse_row_text);
            /*
            this.deleteBtn = view.findViewById(R.id.deleteBtn);

            this.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Home.database.verseDao().delete((int) getItemId());
                    Home.adapter.reload();
                }
            });

             */
        }
    }

    public static List<Verse> verse = new ArrayList<>();

    @NonNull
    @Override
    public VerseAdapter.VerseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.verse_row, parent, false);

        return new VerseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerseAdapter.VerseHolder holder, int position) {
        Verse current = verse.get(position);
        holder.containerView.setTag(current);
        holder.textView.setText(current.contents);
    }

    @Override
    public int getItemCount() {
        int verseC = verse.size();
        Log.e("emmanuel", "count: " + String.valueOf(verseC));
        return verse.size();
    }

    public void reload() {
        verse = Home.database.verseDao().getAllVerses();
        notifyDataSetChanged();
    }
}
