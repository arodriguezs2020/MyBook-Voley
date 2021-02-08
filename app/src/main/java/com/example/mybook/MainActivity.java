package com.example.mybook;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView titleText;
    TextView authorsText;
    EditText query;

    // Base URL for Books API.
    private static final String BOOK_BASE_URL =
            "https://www.googleapis.com/books/v1/volumes";
    // Parameter for the search string.
    private static final String QUERY_PARAM = "q";
    // Parameter that limits search results.
    private static final String MAX_RESULTS = "maxResults";
    // Parameter to filter by print type.
    private static final String PRINT_TYPE = "printType";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleText = findViewById(R.id.tv_title);
        authorsText = findViewById(R.id.tv_authors);
        query = findViewById(R.id.edit_query);
    }

    public void submit(View v){
        String queryString = query.getText().toString();
        if (queryString.length() == 0) {
            displayToast(getString(R.string.enter_field));
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);

            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, queryString)
                    .appendQueryParameter(MAX_RESULTS, "10")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            String url = builtURI.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, (String) null, new
                            Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    parseJSONResponse(response);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            titleText.setText(R.string.not_working);
                        }
                    });
            queue.add(jsonObjectRequest);
        }
    }

    private void parseJSONResponse(JSONObject response) {
        try {
            JSONArray itemsArray = response.getJSONArray("items");

            int i = 0;
            String title = null;
            String authors = null;
            while (i < itemsArray.length() &&
                    (authors == null && title == null)) {
                // Get the current item information.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Move to the next item.
                i++;
            }

            if (title != null && authors != null) {
                titleText.setText(title);
                authorsText.setText(authors);
            } else {
                titleText.setText(getString(R.string.not_results));
            }

        } catch (Exception e) {
            titleText.setText(R.string.not_results);
        }
    }

    private void displayToast(String string) {
        Toast.makeText(this,string,Toast.LENGTH_LONG).show();
    }
}