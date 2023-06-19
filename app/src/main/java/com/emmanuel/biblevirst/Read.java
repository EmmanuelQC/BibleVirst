package com.emmanuel.biblevirst;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Read#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Read extends Fragment implements AdapterView.OnItemSelectedListener {
    //TODO fix bugs: saving to big verses, allow chapter search, make spinner verse empty
    private boolean nextprevPressed = false;
    private boolean isChapter = false;

    private TextView textViewM;
    private TextView verseRef;
    boolean saved = false;
    boolean Rverse = false;
    boolean searched = false;
    String out;
    private String w_verse = "Gensis 1:1"; //placeholder for verse reference!= null
    //the verse number of verse in the text
    private int ChapNumI;
    private String bookName;

    private RequestQueue queueM;

    private String urlS;
    private String search_verse;

    private String urlB;

    public ArrayAdapter<CharSequence> adapterT;
    public Spinner spinnerT;
    public static boolean KJV = false;
    public static boolean WEB = true;
    public static boolean NET = false;

    public Spinner spinner;
    public ArrayAdapter<CharSequence> adapter;

    //API
    String verse = "";
    String ref = "";
    String translation = "";

    //spinner search
    private String book = "Genesis";

    private EditText textViewC;
    private String ChapNum;

    private EditText textViewV;
    private String VerseNum;
    public ArrayAdapter<CharSequence> adapterV;
    public Spinner spinnerV;

    private Button spinBtn;

    private EditText verseInput;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Read() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Read.
     */
    // TODO: Rename and change types and number of parameters
    public static Read newInstance(String param1, String param2) {
        Read fragment = new Read();
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
        queueM = Volley.newRequestQueue(getActivity());

        if (textViewM != null) {
            loadVerseSearch("Genesis");
        }

        Home.adapter.reload();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //starts with Genesis needed for both apis
        urlS = "https://bible-api.com/" + "Genesis 1:1" + "?verse_numbers=true";
        urlB = "https://labs.bible.org/api/?passage=" + "Genesis 1:1" + "&type=json";

        //Translation SPINNER
        spinnerT = (Spinner) getView().findViewById(R.id.translation_spinner);
        spinnerT.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        adapterT = ArrayAdapter.createFromResource(getContext(),
                R.array.translation, android.R.layout.simple_spinner_item);
        adapterT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerT.setAdapter(adapterT);
        spinnerT.setPrompt("Versions");

        //TESTAMENT SPINNER
        spinner = (Spinner) getView().findViewById(R.id.testament_spinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.new_old, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //VERSE SPINNER
        spinnerV = (Spinner) getView().findViewById(R.id.verse_spinner);
        spinnerV.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        adapterV = ArrayAdapter.createFromResource(getContext(),
                R.array.Oldbook_array, android.R.layout.simple_spinner_item);
        adapterV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerV.setAdapter(adapterV);

        //spinner search
        textViewC = (EditText) getView().findViewById(R.id.Cnum);
        textViewV = (EditText) getView().findViewById(R.id.Vnum);

        /*
        Spin Button to search the verse from the spinners
         */
        spinBtn = getView().findViewById(R.id.spinSearch);
        spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextprevPressed = false;

                Log.e("emmanuel", "search btn clicked");
                ChapNum = textViewC.getText().toString();
                //Log.e("emmanuel", ChapNum);
                VerseNum = textViewV.getText().toString();
                //Log.e("emmanuel", VerseNum);


                isChapterCheck(0, VerseNum);

                if (isChapter == true) {
                    w_verse = book + " " + ChapNum; //fixes searching chapter from spinners
                    //isChapter = true;
                    //Log.e("emmanuel", VerseNum + " :is nothing");
                }
                else {
                    w_verse = book + " " + ChapNum + ":" + VerseNum;
                    //isChapter = false;
                }
                Log.e("emmanuel", "isChapter = " + String.valueOf(isChapter));

                urlS = "https://bible-api.com/" + w_verse + "?verse_numbers=true";
                urlB = "https://labs.bible.org/api/?passage=" + w_verse + "&type=json";
                //loadVerseSearch(whole_verse);
                translation_check(w_verse);
                /*
                if (ChapNum != null && VerseNum != null) {
                    ChapNum = textViewC.getText().toString();
                    //Log.e("emmanuel", ChapNum);
                    VerseNum = textViewV.getText().toString();
                    //Log.e("emmanuel", VerseNum);

                    String whole_verse = book + " " + ChapNum + ":" + VerseNum;
                    urlS = "https://bible-api.com/" + whole_verse + "?verse_numbers=true";
                    loadVerseSearch(whole_verse);
                }
                else {
                    Rverse = false;
                    ChapNum = textViewC.getText().toString();
                    //Log.e("emmanuel", ChapNum);
                    VerseNum = textViewV.getText().toString();
                    //Log.e("emmanuel", VerseNum);

                    textViewM.setText("Please wait, then try again!");
                }
                 */
            }
        });

        verseRef = (TextView) getView().findViewById(R.id.bible_verse_ref);

        textViewM = (TextView) getView().findViewById(R.id.bible_verse_more);

        /*Saving Verses from tapping text*/
        textViewM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("emmanuel", "saving verse");
                if (Home.database != null && Home.adapter != null && saved == false && Rverse == true && ref != null) {
                    //checking if verse it too large (whether it has a colon)
                    String colon = ":";
                    String dash = "-";
                    boolean hasColon = false;
                    boolean hasDash = false;
                    char[] j = ref.toCharArray();
                    //Log.e("emmanuel", "ref: " + ref);
                    for (int i = 0; i < ref.length(); i++) {
                        char b = j[i];
                        //Log.e("emmanuel", String.valueOf(b));
                        if (String.valueOf(b).compareTo(colon) == 0) {
                            hasColon = true;
                        }
                        if (String.valueOf(b).compareTo(dash) == 0) {
                            hasDash = true;
                        }
                    }
                    //can save it is not the whole chapter
                    if (hasColon == true) {
                        //Home.adapter = new VerseAdapter();
                        String textOfText = textViewM.getText().toString();
                        String saveTextWhole = textOfText + ref + " " + translation;
                        if (saveTextWhole != null) {
                            Home.database.verseDao().create(saveTextWhole);
                            Log.e("emmanuel", "saved: " + saveTextWhole);
                            Home.database.verseDao().save(saveTextWhole, getId());
                            //Home.adapter.notifyDataSetChanged();
                        }
                        Home.adapter.reload();
                        Snackbar.make(view, "Verse Saved!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        saved = true;
                    }
                    else {
                        Snackbar.make(view, "You can't save whole chapter! Try a verse!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
                else if(saved == true) {
                    Snackbar.make(view, "Verse Already Saved!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        verseInput = (EditText) getView().findViewById(R.id.verse_search);


        /*
        Search Button
         */
        Button verseSearchBtn = getView().findViewById(R.id.submit_search);
        verseSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searched = true;
                nextprevPressed = false;
                search_verse = verseInput.getText().toString();
                isChapterCheck(1, search_verse); //calls 1 function of function to check if it has a colon to determine whether it is a chapter or not
                urlS = "https://bible-api.com/" + search_verse + "?verse_numbers=true";
                urlB = "https://labs.bible.org/api/?passage=" + search_verse + "&type=json";
                //loadVerseMore();
                translation_check(search_verse);
            }
        });

        /*
        Verse Control Buttons
         */
        Button nextVerseBtn = getView().findViewById(R.id.next_verse);
        nextVerseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextprevPressed = true;
                isChapter = true;
                Log.e("emmanuel", "next btn pressed");
                Log.e("emmanuel", "Chapnum:" + String.valueOf(ChapNumI));
                //String wholeV;
                if (bookName != null && ChapNumI != 0) {
                    ChapNumI = ChapNumI + 1;
                    w_verse = bookName + " " + String.valueOf(ChapNumI);
                    urlS = "https://bible-api.com/" + w_verse + "?verse_numbers=true";
                    urlB = "https://labs.bible.org/api/?passage=" + w_verse + "&type=json";
                    Log.e("emmanuel", urlS);
                    translation_check(w_verse);
                }
                //textViewV.setText("", TextView.BufferType.EDITABLE);
            }
        });


        Button prevVerseBtn = getView().findViewById(R.id.prev_verse);
        prevVerseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextprevPressed = true;
                isChapter = true;
                Log.e("emmanuel", "next btn pressed");
                //String wholeV;
                ChapNumI = ChapNumI - 1;
                if (bookName != null && ChapNumI != 0) {
                    w_verse = bookName + " " + String.valueOf(ChapNumI);
                    urlS = "https://bible-api.com/" + w_verse + "?verse_numbers=true";
                    urlB = "https://labs.bible.org/api/?passage=" + w_verse + "&type=json";
                    Log.e("emmanuel", urlS);
                    translation_check(w_verse);
                }
                //textViewV.setText("", TextView.BufferType.EDITABLE);
            }
        });
    }

    /* Spinner operations */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.testament_spinner:
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String testament = parent.getItemAtPosition(position).toString();
                if (testament.compareTo("Old Testament") == 0) {
                    adapterV = ArrayAdapter.createFromResource(getContext(),
                            R.array.Oldbook_array, android.R.layout.simple_spinner_item);
                    spinnerV.setAdapter(adapterV);
                    //changes spinner selection to bookName when searched
                    if (bookName != null && searched == true) {
                        spinnerV.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerV.setSelection(adapterV.getPosition(bookName));
                                searched = false;
                                //Log.e("emmanuel", String.valueOf(adapterV.getPosition(bookName)));
                            }
                        });
                    }
                }
                if (testament.compareTo("New Testament") == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    adapterV = ArrayAdapter.createFromResource(getContext(),
                            R.array.Newbook_array, android.R.layout.simple_spinner_item);
                    spinnerV.setAdapter(adapterV);
                    if (bookName != null && searched == true) {
                        spinnerV.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerV.setSelection(adapterV.getPosition(bookName));
                                searched = false;
                                //Log.e("emmanuel", String.valueOf(adapterV.getPosition(bookName)));
                            }
                        });
                    }
                }
                break;
            case R.id.verse_spinner:
                if (parent.getChildAt(0) != null) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                }
                //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                book = parent.getItemAtPosition(position).toString();
                break;
            case R.id.translation_spinner:
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String transl = parent.getItemAtPosition(position).toString();
                if (transl.compareTo("KJV") == 0) {
                    KJV = true;
                    WEB = false;
                    NET = false;
                    translation_check(w_verse);
                }
                if (transl.compareTo("WEB") == 0) {
                    KJV = false;
                    WEB = true;
                    NET = false;
                    translation_check(w_verse);
                }
                if (transl.compareTo("NET") == 0) {
                    KJV = false;
                    WEB = false;
                    NET = true;
                    translation_check(w_verse);
                }
                break;
        }
    }

    public void translation_check(String v) {
        if (KJV == true) {
            urlS = urlS + "&translation=kjv";
            loadVerseSearch(v);
        }
        if (WEB == true) {
            urlS.replace("&translation=kjv", "");
            loadVerseSearch(v);
            //urlS = urlS + "&translation=web";
        }
        if (NET == true) {
            loadVerseNET();
            Log.e("emmanuel", urlB);
            //urlS = urlS + "&translation=web";
        }
    }

    public void isChapterCheck(int o, String v) {
        if (o == 0) {
            if (v == null || v.compareTo("") == 0) isChapter = true;
            else isChapter = false;
        }
        else if (o == 1) {
            String colon = ":";
            char[] j = v.toCharArray();
            boolean hasColon = false;
            for (int i = 0; i < v.length(); i++) {
                char b = j[i];
                //Log.e("emmanuel", String.valueOf(b));
                if (String.valueOf(b).compareTo(colon) == 0) {
                    hasColon = true;
                }
            }

            isChapter = !hasColon;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //parsing JSON object
    public void loadVerseSearch(final String Sverse) {
        //textViewM.setText("");
        saved = false;
        /* Checking translation selected */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlS,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray versesJA;
                        //comparing strings
                        String versescmp = "verses";
                        String versecmp = "verse";
                        String textcmp = "text";
                        String refcmp = "reference";
                        String tcomp = "translation_id";
                        Iterator<String> iter = response.keys();
                        /* Iterates through the first JSON object in the response */
                        while (iter.hasNext()) {
                            String key = iter.next();
                            //Log.e("emmanuel", key);
                            try {
                                Rverse = true;
                                //Log.d("emmanuel", response.toString());
                                String content = response.get(key).toString();
                                if (content.compareTo("") == 0) {
                                    Snackbar.make(getView(), "That's not a real verse, try something else!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    //textViewM.setText("That's not a real verse, try something else!");
                                }
                                /* searching for chapter number and book name */
                                else if (key.compareTo(versescmp) == 0) {
                                    versesJA = (JSONArray) response.get(key);
                                    int iterationofVerse = 0;
                                    for (int i = 0; i < versesJA.length(); i++) {
                                        JSONObject object = versesJA.getJSONObject(i);
                                        Iterator<String> iterV = object.keys();
                                        String chapcmp = "chapter";
                                        String bookcmp = "book_name";
                                        /* Iterates through the JSON array of the verses details */
                                        while (iterV.hasNext()) {
                                            String keyV = iterV.next();
                                            try {
                                                String contentV = object.get(keyV).toString();
                                                if (keyV.compareTo(chapcmp) == 0) {
                                                    ChapNum = contentV;
                                                    textViewC.setText(ChapNum, TextView.BufferType.EDITABLE);
                                                    ChapNumI = Integer.parseInt(contentV);
                                                    //Log.e("emmanuel", contentV);
                                                }
                                                if (keyV.compareTo(versecmp) == 0) {
                                                    VerseNum = contentV;
                                                    if (nextprevPressed == false || isChapter == false && iterationofVerse == 0) {
                                                        Log.e("emmanuel", "its false");
                                                        textViewV.setText(VerseNum, TextView.BufferType.EDITABLE);
                                                    }
                                                    else if (nextprevPressed == true || isChapter == true && iterationofVerse == 0){
                                                        Log.e("emmanuel", "its true");
                                                        iterationofVerse = 1;
                                                        textViewV.setText("", TextView.BufferType.EDITABLE);
                                                    }
                                                    //Log.e("emmanuel", contentV);
                                                }
                                                if (keyV.compareTo(bookcmp) == 0) {
                                                    bookName = contentV;
                                                    //Log.e("emmanuel", contentV);
                                                }
                                            } catch (JSONException e) {
                                                Log.e("emmanuel", "Could not get chapter number");
                                            }
                                        }
                                    }
                                }
                                else if (key.compareTo(refcmp) == 0) {
                                    ref = content;
                                    verseRef.setText(ref);
                                    //Log.e("emmanuel", ref);
                                }
                                else if (key.compareTo(textcmp) == 0) {
                                    verse = content;
                                    textViewM.setText(verse);
                                    //Log.e("emmanuel", verse);
                                }
                                else if (key.compareTo(tcomp) == 0) {
                                    translation = content.toUpperCase();
                                    String no_translation = verseRef.getText().toString();
                                    verseRef.setText(no_translation + " " + translation);
                                    //Log.e("emmanuel", translation);
                                }
                            } catch (JSONException e) {
                                Rverse = false;
                                Log.e("emmanuel", "Could not get keys");
                            }
                        }
                        //checking if the book is not in the right testament which causes the position in the adapter to be -1
                        if (adapterV.getPosition(bookName) == -1 && spinner.getSelectedItemPosition() == 0) {
                            spinner.post(new Runnable() {
                                @Override
                                public void run() {
                                    //switches to the new testament if in the old testament
                                    spinner.setSelection(adapter.getPosition("New Testament"));

                                }
                            });
                        }
                        else if (adapterV.getPosition(bookName) == -1 && spinner.getSelectedItemPosition() == 1){
                            spinner.post(new Runnable() {
                                @Override
                                public void run() {
                                    spinner.setSelection(adapter.getPosition("Old Testament"));
                                }
                            });
                        }
                        //changing the verse spinner after or if it is in the right book
                        spinnerV.post(new Runnable() {
                            @Override
                            public void run() {
                                spinnerV.setSelection(adapterV.getPosition(bookName));
                                //Log.e("emmanuel", String.valueOf(adapterV.getPosition(bookName)));
                            }
                        });
                        //Log.e("emmanuel", ChapNum + VerseNum);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (Sverse.compareTo("") == 0) {
                    Rverse = false;
                    Snackbar.make(getView(), "Please input something! eg. John 3:16", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //textViewM.setText("Please input something! eg. John 3:16");
                }
                else if (ChapNum.compareTo("333") == 0 && VerseNum.compareTo("777") == 0) {
                    Snackbar.make(getView(), "Congrats! You found the easter egg! God bless!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Rverse = false;
                    Snackbar.make(getView(), "That didn't work! Make sure to be specific! eg. 1 John 3:16", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //textViewM.setText("That didn't work! Make sure to be specific! eg. John 3:16");
                }
            }
        });

        // Add the request to the RequestQueue.
        queueM.add(jsonObjectRequest);

        w_verse = ref + " " + translation;
        //verseRef.setText(wholeVR);
        Log.e("emmanuel", w_verse);
        out = verse + ref + " " + translation;
        Log.e("emmanuel", "out: " + out);
        //textViewM.setText(out);
        /*
        if (translation.compareTo("") != 0 && verse.compareTo("") != 0 && ref.compareTo("") != 0) {
            out = verse + ref + " " + translation;
            //Log.e("emmanuel", out);
            textViewM.setText(out);
        }

         */
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
                                    int iterationofVerse = 0;
                                    String keyV = iterV.next();
                                    String contentV = object.get(keyV).toString();
                                    if (keyV.compareTo(chapcmp) == 0) {
                                        ChapNum = contentV;
                                        textViewC.setText(ChapNum, TextView.BufferType.EDITABLE);
                                        ChapNumI = Integer.parseInt(contentV);
                                        //Log.e("emmanuel", contentV);
                                    }
                                    if (keyV.compareTo(versecmp) == 0) {
                                        VerseNum = contentV;
                                        if (nextprevPressed == false || isChapter == false && iterationofVerse == 0) {
                                            textViewV.setText(VerseNum, TextView.BufferType.EDITABLE);
                                        }
                                        else if (nextprevPressed == true || isChapter == true && iterationofVerse == 0) { //didn't use else because there still may be other conditions
                                            textViewV.setText("", TextView.BufferType.EDITABLE);
                                            iterationofVerse = 1;
                                            //nextprevPressed = false;
                                        }
                                        //Log.e("emmanuel", contentV);
                                    }
                                    else if (keyV.compareTo(textcmp) == 0) {
                                        verse = contentV;
                                        if (i == 0) textViewM.setText(verse);
                                        else textViewM.append("("+VerseNum + ") " + verse);
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

                        //VerseNum = textViewV.getText().toString();
                        //isChapterCheck(0, verse);

                        if (VerseNum == null || VerseNum == "" || nextprevPressed) {
                            Log.e("emmanuel", "Verse number: " + VerseNum);
                            ref = book + " " + ChapNum; //fixes searching chapter from spinners
                        }
                        else if (!isChapter) {
                            ref = bookName + " " + ChapNum + ":" + VerseNum;
                            Log.e("emmanuel", "its false");
                        }
                        w_verse = ref;
                        verseRef.setText(ref + " " + "NET");

                        //checking if the book is not in the right testament which causes the position in the adapter to be -1
                        if (adapterV.getPosition(bookName) == -1 && spinner.getSelectedItemPosition() == 0) {
                            spinner.post(new Runnable() {
                                @Override
                                public void run() {
                                    //switches to the new testament if in the old testament
                                    spinner.setSelection(adapter.getPosition("New Testament"));

                                }
                            });
                        }
                        else if (adapterV.getPosition(bookName) == -1 && spinner.getSelectedItemPosition() == 1){
                            spinner.post(new Runnable() {
                                @Override
                                public void run() {
                                    spinner.setSelection(adapter.getPosition("Old Testament"));
                                }
                            });
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (verse.compareTo("") == 0) {
                    Rverse = false;
                    Snackbar.make(getView(), "Please input something! eg. John 3:16", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //textViewM.setText("Please input something! eg. John 3:16");
                }
                else if (ChapNum.compareTo("333") == 0 && VerseNum.compareTo("777") == 0) {
                    Snackbar.make(getView(), "Congrats! You found the easter egg! God bless!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Rverse = false;
                    Snackbar.make(getView(), "That didn't work! Make sure to be specific! eg. 1 John 3:16", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    //textViewM.setText("That didn't work! Make sure to be specific! eg. John 3:16");
                }
            }
        });
        // Add the request to the RequestQueue.
        queueM.add(jsonArrayRequest);
    }
}