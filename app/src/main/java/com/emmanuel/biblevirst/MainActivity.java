package com.emmanuel.biblevirst;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    //public static VerseDatabase database;

    /*
    private RecyclerView recyclerView;
    public static VerseAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
     */

    private TextView textView;
    private TextView textView1;
    private TextView textViewM;

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;

    private Fragment Home;

    Home homeFrag;
    public static Saved savedFrag;
    Read readFrag;

    public static ViewPagerAdapter adapterBNV;

    //TODO: add button to save verse, make Home buttons more compact(change colours), refine reading fragment(make it look nicer), add button to search online about verse, verse categories (eg. Uplifting, apologetics, sadness, videos), navigation)
    //could not make function for api as each api was a unique JSONObject format(optimisation)
    //I even had a dream of programming this app
    //Need to deal with the different strings of verses and its verse number(cannot convert dash or : to integer)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        database = Room.databaseBuilder(getApplicationContext(), VerseDatabase.class, "verses")
                .allowMainThreadQueries()
                .build();

        recyclerView = findViewById(R.id.recycler_view_main);
        layoutManager = new LinearLayoutManager(this);
        adapter = new VerseAdapter();

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.reload();

         */

        textView = findViewById(R.id.bible_verse);
        textView1 = findViewById(R.id.bible_verse_book);
        textViewM = findViewById(R.id.bible_verse_more);

        viewPager2 = findViewById(R.id.viewpager2);

        //adapterBNV.notifyItemChanged(2);

        bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.home:
                                viewPager2.setCurrentItem(0,false);
                                break;
                            case R.id.read:
                                viewPager2.setCurrentItem(1,false);
                                break;
                            case R.id.saved:
                                viewPager2.setCurrentItem(2,false);
                                break;
                        }
                        return false;
                    }
                });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.read).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.saved).setChecked(true);
                        break;
                }
            }
        });

        setupViewPager(viewPager2);

        /*
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMth);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new Home()).commit();
        if (savedInstanceState != null) {
            //Restore the fragment's instance
            Home = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        }

         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_btn:
                startActivity(new Intent(this, Info_Activity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager2 viewPager) {

        adapterBNV = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        homeFrag = new Home();
        savedFrag = new Saved();
        readFrag = new Read();

        adapterBNV.addFragment(homeFrag);
        adapterBNV.addFragment(readFrag);
        adapterBNV.addFragment(savedFrag);

        viewPager.setAdapter(adapterBNV);

        int p = adapterBNV.getItemPosition(savedFrag);
        long i = adapterBNV.getItemId(p);
        Log.e("emmaneul", "position" + String.valueOf(p));
        Log.e("emmaneul", "id: " + String.valueOf(i));
    }

    /*
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "HomeFragment", Home);
    }

     */



    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMth = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;
                    switch(item.getItemId()) {
                        case R.id.read:
                            fragment = new Read();
                            break;
                        case R.id.saved:
                            fragment = new Saved();
                            break;
                        case R.id.home:
                            fragment = new Home();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    return true;
                }
            };

    public Object jsonObject(JSONObject results) {
        Object object = null;
        Iterator<String> iter = results.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            Log.e("emmanuel", key);
            try {
                object = results.get(key);
                return object;
            } catch (JSONException e) {
                Log.e("emmanuel", "Could not get keys");
            }
        }
        return 0;
    }
}