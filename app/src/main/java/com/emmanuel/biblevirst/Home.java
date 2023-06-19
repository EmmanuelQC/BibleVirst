package com.emmanuel.biblevirst;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

//TODO add about tab(fragment)?, add websearch about verse, add save searched verse, add different versions search and random verses
//TODO save only one verse limit, disallow slide

//TODO bug fixes - allow slide for three button in home, fix crash bugs, *add diff fucntion to not allow to large portions of verses, search verse when version is changes, search chapter when verse hunmber is not inputted

/*
TODO save NET verse, get NET chapter, develop frontend, random verses random
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    boolean saved = false;
    public static VerseDatabase database;
    public static VerseAdapter adapter;

    private TextView textView;
    private TextView textView1;
    private TextView textViewM;

    private RequestQueue queue;
    private RequestQueue queueM;

    private String url;
    private String urlM;
    private String urlS;

    private String urlB = "https://labs.bible.org/api/?passage=votd&type=json";
    private String ChapNum;
    private String VerseNum;
    private String bookName;

    public static String rVerse;
    private String bookWithBookAd;
    private String bookWV = ""; //whole verse
    private String search_verse;

    private EditText verseInput;

    private int verseNumM = 0;
    private int verseNum = 0;
    private int setURL = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(getActivity());
        queueM = Volley.newRequestQueue(getActivity());
        url ="https://beta.ourmanna.com/api/v1/get/?format=json&order=random";
        //urlM = "https://bible-api.com/genesis 1:1-12";

        adapter = new VerseAdapter();
        adapter.reload();

        loadVerse();
        //loadVerseNET();
        //loadVerseMore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = Room
                .databaseBuilder(getActivity().getApplicationContext(), VerseDatabase.class, "verses")
                .allowMainThreadQueries()
                .build();

        //int id = database.verseDao().verseId();
        //Log.e("emmanuel", "ids: "+ String.valueOf(id));
        //database.verseDao().deleteAll();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MainActivity.viewPager2.setOffscreenPageLimit(2);
        if (savedInstanceState != null) {
            url = savedInstanceState.getString("verseKey", "https://beta.ourmanna.com/api/v1/get/?format=json&order=random");
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView) getView().findViewById(R.id.bible_verse);
        textView1 = (TextView) getView().findViewById(R.id.bible_verse_book);
        textViewM = (TextView) getView().findViewById(R.id.bible_verse_more);

        verseInput = (EditText) getView().findViewById(R.id.verse_search);

        Button saveVerseBtn = getView().findViewById(R.id.save_verse);
        saveVerseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("emmanuel", "save verse btn clicked");
                if (database != null && adapter != null && saved == false) {
                    database.verseDao().create(rVerse);
                    if (rVerse != null) {
                        Log.e("emmanuel", rVerse);
                        database.verseDao().save(rVerse, getId());
                    }
                    adapter.reload();
                    Snackbar.make(view, "Verse Saved!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    saved = true;
                }
                else if(saved == true) {
                    Snackbar.make(view, "Verse Already Saved!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(view, "Try Again!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        Button getNewVerseBtn = getView().findViewById(R.id.get_new_verse);
        getNewVerseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saved = false;
                //int ran = new Random().nextInt(2);
                loadVerse();
            }
        });

        TextView dailyVersetext = getView().findViewById(R.id.daily_verse);
        dailyVersetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadVerseNET();
            }
        });

        Button verseSearchBtn = getView().findViewById(R.id.submit_search);
        verseSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_verse = verseInput.getText().toString();
                urlS = "https://bible-api.com/" + search_verse + "?verse_numbers=true";
                loadVerseSearch();
            }
        });

        Button getVerseBtn = getView().findViewById(R.id.get_verse);
        getVerseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setURL++;
                loadVerseMore();
            }
        });
    }

    public void loadVerse() {
        //textView.setText("");
        //textView1.setText("");
        // Request a jsonobject response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject results = null;
                        JSONObject details = null;
                        //get's the whole result object
                        try {
                            results = response.getJSONObject("verse");
                            //Log.d("emmanuel", "results: " + results);
                        } catch (JSONException e) {
                            Log.e("emmanuel", "Could not get verse");
                        }
                        Iterator<String> iter = results.keys();
                        while (iter.hasNext()) {
                            String detailscmp = "details";
                            String key = iter.next();
                            //Log.e("emmanuel", key);

                            if (key.compareTo(detailscmp) == 0) {
                                try {
                                    details = results.getJSONObject(key);
                                    //Log.d("emmanuel", "details: " + details);
                                } catch (JSONException e) {
                                    Log.e("emmanuel", "Could not get details");
                                }
                                //iterates over details
                                Iterator<String> iterDetails = details.keys();
                                String version = "NIV";
                                String book = "";
                                while (iterDetails.hasNext()) {
                                    String keyDe = iterDetails.next();
                                    //Log.d("emmanuel", keyDe);
                                    try {
                                        //cmp strings
                                        String verseString = "text";
                                        String verseBook = "reference";
                                        String verseBookVersion = "version";
                                        //values of detail keys
                                        String value = details.get(keyDe).toString();
                                        //Log.d("emmanuel", "value: " + value);
                                        if (keyDe.compareTo(verseString) == 0) {
                                            rVerse = value;
                                            textView.setText("" + value);
                                            //Log.d("emmanuel", "value: " + value);
                                        }
                                        if (keyDe.compareTo(verseBook) == 0) {
                                            book = value;
                                        }
                                        if (keyDe.compareTo(verseBookVersion) == 0) {
                                            version = value;
                                        }
                                    } catch (JSONException e) {
                                        Log.e("emmanuel", "Could not get text", e);
                                    }
                                }
                                rVerse = rVerse + "\n" + book + " " + version;
                                //urlM = "https://bible-api.com/" + book;
                                textView1.setText("" + book + " " + version);

                                int bookLen = book.length();
                                String colon = ":";

                                for (int i = 0; i < bookLen; i++) {
                                    char[] j = book.toCharArray();
                                    char b = j[i];
                                    if (String.valueOf(b).compareTo(colon) == 0) {
                                        Log.e("emmanuel", bookWV);
                                        bookWV = book.substring(0, i);
                                    }
                                }
                                urlM = "https://bible-api.com/" + bookWV + "?verse_numbers=true";

                                //find book number and gets more of it
                                /*
                                int bookLen = book.length();
                                int B = 0;
                                int dPos = 0;
                                int cL = 0;
                                String BnumS;
                                String bookWV; //whole verse
                                String colon = ":";
                                String dash = "-";
                                boolean hasDash = false;
                                for (int i = 0; i < bookLen; i++) {
                                    char[] j = book.toCharArray();
                                    char b = j[i];
                                    if (String.valueOf(b).compareTo(colon) == 0) {
                                        B = i+1;
                                        cL = i;
                                        bookWV = book.substring(0, i);
                                    }
                                    if (String.valueOf(b).compareTo(dash) == 0) {
                                        hasDash = true;
                                        dPos = i+1;
                                        cL = i;
                                    }
                                }
                                if (hasDash == true)
                                {
                                    BnumS = book.substring(dPos, bookLen);
                                    verseNum = Integer.valueOf(BnumS);
                                    verseNumM = verseNum + 3;
                                    bookNoVerse = book.substring(0,cL);
                                    bookWithBookAd = bookNoVerse + "-" + String.valueOf(verseNumM);
                                    Log.e("emmanuel", bookNoVerse);
                                    Log.e("emmanuel", "https://bible-api.com/" + bookWithBookAd);
                                    urlM = "https://bible-api.com/" + bookWithBookAd;
                                    //setURL++;
                                }
                                if (hasDash == false)
                                {
                                    BnumS = book.substring(B, bookLen);
                                    verseNum = Integer.valueOf(BnumS);
                                    verseNumM = verseNum + 3; //adds three to view the other versus
                                    //Log.e("emmanuel", String.valueOf(B));
                                    Log.e("emmanuel", BnumS);
                                    Log.e("emmanuel", String.valueOf(verseNum));
                                    //Log.e("emmanuel", book.substring(verseNum, bookLen));

                                    //int noVerseNum = book.length() - Integer.bitCount(verseNum);
                                    bookNoVerse = book.substring(0, cL);
                                    bookWithBookAd = bookNoVerse + ":" + String.valueOf(verseNum) + "-" + verseNumM;
                                    Log.e("emmanuel", String.valueOf(verseNum));
                                    Log.e("emmanuel", "https://bible-api.com/" + bookWithBookAd);

                                    urlM = "https://bible-api.com/" + bookWithBookAd;
                                    //setURL++;
                                }
                                */
                            }
                        }
                        /*try {
                            JSONObject results = response.getJSONObject("verse");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject result = results.getJSONObject(String.valueOf(i));
                                textView.setText("Verse:\n" + results.getJSONObject("text"));
                                textView1.setText("" + results.getJSONObject("reference"));
                            }
                            // Display the first 500 characters of the response string.
                        } catch (JSONException e) {
                            Log.e("cs50", "Pokemon JSON error: ", e);
                        } */
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "Make sure you are connected to the internet!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                //textView.setText("Make sure you are connected to the internet!");
                Log.e("emmanuel", "Bruhh it dont work");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public void loadVerseNET() {
        //textViewM.setText("");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlB,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String versecmp = "verse";
                        String textcmp = "text";
                        String chapcmp = "chapter";
                        String bookcmp = "bookname";
                        String verse = null;
                        Log.d("emmanuel", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                Iterator<String> iterV = object.keys();
                                while (iterV.hasNext()) {
                                    String keyV = iterV.next();
                                    String contentV = object.get(keyV).toString();
                                    if (keyV.compareTo(chapcmp) == 0) {
                                        ChapNum = contentV;
                                        //textView1.setText(ChapNum, TextView.BufferType.EDITABLE);
                                        //ChapNum = Integer.parseInt(contentV);
                                        //Log.e("emmanuel", contentV);
                                    }
                                    if (keyV.compareTo(versecmp) == 0) {
                                        VerseNum = contentV;
                                    }
                                    else if (keyV.compareTo(textcmp) == 0) {
                                        verse = contentV;
                                        if (i == 0) textView.setText(verse);
                                        else textView.append("("+VerseNum + ") " + verse);
                                        //Log.e("emmanuel", verse);
                                    }
                                    if (keyV.compareTo(bookcmp) == 0) {
                                        bookName = contentV;
                                        //Log.e("emmanuel", contentV);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String reference = bookName + " " + ChapNum + ":" + VerseNum;
                        textView1.setText(reference + " " + "NET");
                        urlM = "https://bible-api.com/" + reference + "?verse_numbers=true";
                        //ref = bookName + " " + ChapNum + ":" + VerseNum;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("emmanuel", "NET didn't work");
            }
        });
        // Add the request to the RequestQueue.
        queueM.add(jsonArrayRequest);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("verseKey", url);
    }

    public void loadVerseMore() {
        //textViewM.setText("");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlM,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String textcmp = "text";
                        String verse = null;
                        Iterator<String> iter = response.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            //Log.e("emmanuel", key);
                            try {
                                //Log.d("emmanuel", response.toString());
                                verse = response.get(key).toString();
                                if (key.compareTo(textcmp) == 0 && setURL >= 1) {
                                    Log.e("emmanuel", bookWV);
                                    textViewM.setText(verse + bookWV.toUpperCase() + " WEB");
                                }
                                else if (key.compareTo(textcmp) == 0) {
                                    textViewM.setText(verse + "Genesis 1:1-12 WEB");
                                }
                            } catch (JSONException e) {
                                Log.e("emmanuel", "Could not get keys");
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    verseNumM--;
                    bookWithBookAd = bookWV + ":" + String.valueOf(verseNum) + "-" + verseNumM;
                    urlM = "https://bible-api.com/" + bookWithBookAd;
                } catch (StringIndexOutOfBoundsException e) {
                    Snackbar.make(getView(), "That didn't work!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    //textViewM.setText("That didn't work!");
                }
            }
        });
        // Add the request to the RequestQueue.
        queueM.add(jsonObjectRequest);
    }

    public void loadVerseSearch() {
        textViewM.setText("");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlS,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String textcmp = "text";
                        Iterator<String> iter = response.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            //Log.e("emmanuel", key);
                            try {
                                //Log.d("emmanuel", response.toString());
                                String verse = response.get(key).toString();
                                if (verse.compareTo("") == 0) {
                                    Snackbar.make(getView(), "That's not a real verse, try something else!", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                    //textViewM.setText("That's not a real verse, try something else!");
                                }
                                else if (key.compareTo(textcmp) == 0) {
                                    textViewM.setText(verse + search_verse.toUpperCase() + " WEB");
                                }
                            } catch (JSONException e) {
                                Log.e("emmanuel", "Could not get keys");
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (search_verse.compareTo("") == 0) {
                    Snackbar.make(getView(), "Please input something! eg. John 3:16", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    //textViewM.setText("Please input something! eg. John 3:16");
                }
                else {
                    Snackbar.make(getView(), "That didn't work! Make sure to be specific! eg. John 3:16", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    //textViewM.setText("That didn't work! Make sure to be specific! eg. John 3:16");
                }
            }
        });

        // Add the request to the RequestQueue.
        queueM.add(jsonObjectRequest);
    }
}