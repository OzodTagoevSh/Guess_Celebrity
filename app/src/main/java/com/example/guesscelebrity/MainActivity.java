package com.example.guesscelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebrityNames = new ArrayList<>();
    ArrayList<String> celebrityUrls = new ArrayList<>();
    ArrayList<String> answers = new ArrayList<>();
    int locationOfCorrectAnswer;
    Button nameButton0;
    Button nameButton1;
    Button nameButton2;
    Button nameButton3;

    ImageView imageView;

    public void chooseButton(View view) {
        String result = "";
        Button clicked = (Button) view;
        if(clicked.getText().toString().equals(answers.get(locationOfCorrectAnswer))) {
            result = "Correct!";
        } else {
            result = "Wrong! It was " + answers.get(locationOfCorrectAnswer);
        }
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        restart();
    }

    public void restart() {
        Random rand = new Random();
        locationOfCorrectAnswer = rand.nextInt(4);
        int size = celebrityUrls.size();
        int k = rand.nextInt(size);
        answers.clear();
        ImageDownloader image = new ImageDownloader();
        Bitmap celebrityImage;
        try {
            celebrityImage = image.execute(celebrityUrls.get(k)).get();

            imageView.setImageBitmap(celebrityImage);
        } catch(Exception e) {
            e.printStackTrace();
        }
        for(int i = 0; i < 4; i++) {
            if(i == locationOfCorrectAnswer) {
                answers.add(celebrityNames.get(k));
            } else {
                int wrongAnswer = rand.nextInt(size);
                while(wrongAnswer == k) {
                    wrongAnswer = rand.nextInt(size);
                }
                answers.add(celebrityNames.get(wrongAnswer));
            }
        }

        nameButton0.setText(answers.get(0));
        nameButton1.setText(answers.get(1));
        nameButton2.setText(answers.get(2));
        nameButton3.setText(answers.get(3));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameButton0 = findViewById(R.id.nameButton0);
        nameButton1 = findViewById(R.id.nameButton1);
        nameButton2 = findViewById(R.id.nameButton2);
        nameButton3 = findViewById(R.id.nameButton3);
        imageView = findViewById(R.id.imageView);

        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("https://allfamous.org/most-popular").get();
            Pattern pattern = Pattern.compile(" alt=\"(.*?), ");
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                celebrityNames.add(matcher.group(1));
            }
            pattern = Pattern.compile(" data-src=\"(.*?)\"");
            matcher = pattern.matcher(result);
            while(matcher.find()) {
                celebrityUrls.add(matcher.group(1));
            }
            celebrityUrls.remove(50);
            celebrityUrls.remove(1);
            celebrityUrls.remove(0);

        } catch(Exception e) {
            e.printStackTrace();
        }
        Log.i("Content", String.valueOf(celebrityUrls));
        restart();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch(Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }
}