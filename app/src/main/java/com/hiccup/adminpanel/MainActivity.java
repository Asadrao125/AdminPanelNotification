package com.hiccup.adminpanel;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    Spinner spinner;
    EditText title,message;
    EditText image;
    Button post,preview;
    String topicname;
    TextView Tv_spinner,prev_title,prev_msg;
    ImageView prev_image;
    Bitmap bmp;
    String image_uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = (EditText) findViewById(R.id.et_title);
        message = (EditText) findViewById(R.id.et_msg);
        image = (EditText) findViewById(R.id.et_image);
        post = (Button) findViewById(R.id.btn_post);
        title = (EditText) findViewById(R.id.et_title);
        spinner = (Spinner) findViewById(R.id.topic_type);
        Tv_spinner=(TextView)findViewById(R.id.spinner_tv);
        preview=(Button)findViewById(R.id.btn_preview);


        ShowSpinner(getResources().getStringArray(R.array.Topics), spinner);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spinner.getSelectedItem().toString().equals("Topics"))
                {
                    TextView errorText = (TextView)spinner.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Please select topic name");}

                else if(title.getText().toString().isEmpty())
                {
                    title.setError("please enter title");
                }

                else if(message.getText().toString().isEmpty())
                {
                    message.setError("please enter message");

                }
                else {
                    sendnotification();
                }
            }
        });


        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showpreviewdialog();
            }
        });
    }
    private void ShowSpinner(String[] items, Spinner spinner)
    {
        // Initializing a String Array
        String[] list = items;

        final List<String> plantsList = new ArrayList<>(Arrays.asList(list));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.support_simple_spinner_dropdown_item, plantsList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.parseColor("#C1C1C1"));
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                topicname = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Topic " + topicname+" selected", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void sendnotification()
    {
        try{


            RequestQueue queue = Volley.newRequestQueue(this);

            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title", title.getText().toString());
            data.put("body",message.getText().toString());
            data.put("image",image.getText().toString());
            System.out.println("image text "+image.getText().toString());
            JSONObject notification_data = new JSONObject();
            Log.i("heo",notification_data.toString());
            notification_data.put(""+topicname, data);
            notification_data.put("to","/topics/"+topicname);

            JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    String api_key_header_value = "Key=AAAAFnI47iI:APA91bHHfBrgraKAXKv8E03pfDPkIZIv023O437fEd28bXTm52sSlpnf-mt1S2hWjp5W6vHuBs1HhTW5smUTtkUwZLb9PnJudAFeJd5cvHEzoU1zEA6gE5G-x1mFifkmWHNax08VmfZt";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", api_key_header_value);
                    return headers;
                }
            };

            queue.add(request);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showpreviewdialog()
    {

        String url=image.getText().toString();
        String uri=url.replaceAll(" ","%25");

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.preview_layout);
        dialog.setTitle("Title...");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        prev_title=(TextView)dialog.findViewById(R.id.notification_title);
        prev_msg=(TextView)dialog.findViewById(R.id.notification_msg);
        prev_image=(ImageView)dialog.findViewById(R.id.prev_image);


        prev_title.setText(title.getText().toString());
        prev_msg.setText(message.getText().toString());
        RelativeLayout relativeLayout=(RelativeLayout)dialog.findViewById(R.id.r2);


        if(!url.isEmpty()) {
            relativeLayout.setVisibility(View.VISIBLE);
            Picasso.get().load(uri)
                    .into(prev_image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                            System.out.println("success hogya");
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

        }
        else
        {
            relativeLayout.setVisibility(View.INVISIBLE);

        }

        dialog.show();

    }

}
