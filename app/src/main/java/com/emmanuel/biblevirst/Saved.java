package com.emmanuel.biblevirst;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import static com.emmanuel.biblevirst.Home.database;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Saved#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Saved extends Fragment {
    private TextView verseC;

    private RecyclerView recyclerView;
    //public static VerseAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    //public static VerseDatabase database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Saved() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Saved.
     */
    // TODO: Rename and change types and number of parameters
    public static Saved newInstance(String param1, String param2) {
        Saved fragment = new Saved();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //MainActivity.adapterBNV.bindViewHolder(, 2);
        //overrides viewpager saved state fragment
        //MainActivity.adapterBNV.notifyItemChanged(-2);
        //MainActivity.savedFrag = new Saved();
        //MainActivity.adapterBNV.saveState();
        //MainActivity.adapterBNV.addFragment(MainActivity.savedFrag);

        //MainActivity.viewPager2.setAdapter(MainActivity.adapterBNV);

        View rootView = inflater.inflate(R.layout.fragment_saved, container, false);
        /*
        Home.database = Room
                .databaseBuilder(getActivity().getApplicationContext(), VerseDatabase.class, "verses")
                .allowMainThreadQueries()
                .build();

         */
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this.getContext());
        Home.adapter = new VerseAdapter();
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(Home.adapter);
        }
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MainActivity.adapterBNV.createFragment(2);

        //MainActivity.viewPager2.removeAllViews();
        //MainActivity.viewPager2.removeViewAt(2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button deleteBtn = getView().findViewById(R.id.deleteAllBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.verseDao().deleteAll();
                Home.adapter.notifyDataSetChanged();
                Home.adapter.reload();
                Snackbar.make(v, "All Saved Verses Removed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Snackbar.make(v, "Slide to delete a saved verse!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Home.adapter.reload();
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int id = viewHolder.getAdapterPosition() + 1;
            //long idT = viewHolder.getItemId();
            VerseAdapter.verse.remove(viewHolder.getAdapterPosition());
            //Log.e("emmanuel", "id: " + String.valueOf(id));
            //Log.e("emmanuel", "idT: " + String.valueOf(idT));
            database.verseDao().delete(id);
            Home.adapter.notifyDataSetChanged();
            Home.adapter.reload();
            Log.e("emmanuel", "verse deleted");
        }
    };
}