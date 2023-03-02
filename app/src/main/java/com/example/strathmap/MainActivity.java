package com.example.strathmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    Button btn_direction;
    Button btn_clear;
    Spinner spn_current_loc;
    Spinner spn_destination;
    ImageView image;

    ArrayList<String> path = new ArrayList<>();
    String test ="Not working";

    HashMap<String,String> roomTable = new HashMap<String, String>();
    HashMap<String, Pair<Integer,Integer>> roomPixels = new HashMap<>();

    public void setRoomTable(HashMap<String, String> roomTable) {
        roomTable.put("Reception", "8520597012");
        roomTable.put("ORT 1","8520597015");
        roomTable.put("ORT 2","8520597014");
        roomTable.put("ENT 1","8520597004");
        roomTable.put("ENT 2","8520597002");
        roomTable.put("ICU 1","8520597007");
        roomTable.put("ICU 2","8520597009");
        roomTable.put("ICU 3","8520597006");
        roomTable.put("ICU 4","8520597008");
        roomTable.put("Ward 1","8520597011");
        roomTable.put("Ward 2","8520617618");
        roomTable.put("Ward 3","8520617619");
        roomTable.put("Ward 4","8520617620");
        roomTable.put("Ward 5","8520617621");
        roomTable.put("Office 1","8520617622");
        roomTable.put("Office 2","8520617623");
        roomTable.put("Dentist 1","8520597013");
        roomTable.put("Dentist 2","8520597005");
        roomTable.put("Dentist 3","8520597003");
        roomTable.put("Toilet 1","8520597016");
        roomTable.put("Toilet 2","8520617617");
    }

    public void setRoomPixels(HashMap<String,Pair<Integer,Integer>> roomPixels){
        roomPixels.put("Reception",new Pair(175,1790));
        roomPixels.put("ORT 1",new Pair(200,2750));
        roomPixels.put("ORT 2",new Pair(400,2733));
        roomPixels.put("Dentist 1",new Pair(600,2713));
        roomPixels.put("Dentist 2",new Pair(950,2725));
        roomPixels.put("Dentist 3",new Pair(1300,2712));
        roomPixels.put("Toilet 2",new Pair(175,2400));
        roomPixels.put("Toilet 1",new Pair(380,2400));
        roomPixels.put("Ward 1",new Pair(600,2419));
        roomPixels.put("Ward 2",new Pair(600,2159));
        roomPixels.put("Ward 3",new Pair(600,1790));
        roomPixels.put("Ward 4",new Pair(600,1420));
        roomPixels.put("Ward 5",new Pair(600,960));
        roomPixels.put("Office 1",new Pair(600,610));
        roomPixels.put("Office 2",new Pair(600,320));
        roomPixels.put("ICU 2",new Pair(930,320));
        roomPixels.put("ICU 4",new Pair(1180,320));
        roomPixels.put("ICU 1",new Pair(1350,320));
        roomPixels.put("ICU 3",new Pair(1350,320));
        roomPixels.put("ENT 1",new Pair(950,2725));
        roomPixels.put("ENT 2",new Pair(1300,2712));
    }

    public Pair<Integer,Integer> getPixels(String room){
        Pair<Integer,Integer> obj = roomPixels.get(room);
        return obj;
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                                responseText.setText("Failed to Connect to Server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String Response="";
                        try {
                            Response = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String[] arr = Response.split(" ");
                        for (String s : arr) {
                            for (String set : roomTable.keySet()) {
                                if (roomTable.get(set).equals(s)) {
                                    path.add(set);
                                }

                            }
                        }
//                                }
                    }

                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRoomTable(roomTable);
        setRoomPixels(roomPixels);

        //Setting current location dropdown
        Spinner current_loc_spinner = findViewById(R.id.current_location);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.room_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        current_loc_spinner.setAdapter(adapter);

        //Setting destination location dropdown
        Spinner destination_spinner = findViewById(R.id.destination);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.room_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destination_spinner.setAdapter(adapter1);

        image = findViewById(R.id.imageView4);


        //Input location
        spn_current_loc = findViewById(R.id.current_location);
        spn_destination = findViewById(R.id.destination);


        //click listerner for clear
        btn_clear=findViewById(R.id.clear);
        btn_clear.setEnabled(false);
        btn_clear.setOnClickListener(v -> {
            path.clear();
            btn_direction.setEnabled(true);
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.layoutcrop3);

            //Create a new image bitmap and attach a brand new canvas to it
            Bitmap tempBitmap1 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Bitmap.Config.RGB_565);
            Canvas tempCanvas1 = new Canvas(tempBitmap1);
            tempCanvas1.drawBitmap(bitmap1, 0, 0, null);
            image.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap1));
            btn_clear.setEnabled(false);

        });

        //Assign value to each control on the layout
        btn_direction = findViewById(R.id.direction);

        //click lister for button1
        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String _from = spn_current_loc.getItemAtPosition(spn_current_loc.getSelectedItemPosition()).toString();
                String _to = spn_destination.getItemAtPosition(spn_destination.getSelectedItemPosition()).toString();

                String from = roomTable.get(_from);
                String to = roomTable.get(_to);

//               String postUrl = "http://192.168.1.3:5000" + "/route" + "?from=8520597008&to=8520597014";
                String postUrl = "http://192.168.1.3:5000" + "/route" + "?from=" + from + "&to=" + to;

                String postBodyText = "Sending route";
                MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
                RequestBody postBody = RequestBody.create(mediaType, postBodyText);

                if(path.isEmpty()) {
                    postRequest(postUrl, postBody); //This sends the rooms, gets the path and save it
                    Toast.makeText(MainActivity.this, "Press once again", Toast.LENGTH_SHORT).show();

                }
                else{
                    btn_clear.setEnabled(true);
                    btn_direction.setEnabled(false);
                }

                // Yellow Paint
                Paint paint = new Paint();
                paint.setARGB(255, 0, 0, 0);
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(25);
                paint.setColor(Color.YELLOW);

                //Red Paint
                Paint startPaint = new Paint();
                startPaint.setARGB(255, 0, 0, 0);
                startPaint.setStyle(Paint.Style.FILL);
                startPaint.setStrokeWidth(50);
                startPaint.setColor(Color.RED);


                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.layoutcrop3);

                //Create a new image bitmap and attach a brand new canvas to it
                Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);

                //Draw the image bitmap into the canvas
                tempCanvas.drawBitmap(bitmap, 0, 0, null);


                //Draw everything else you want into the canvas, in this example a rectangle with rounded edges
                int px=0;
                int py=0;

                ArrayList<Pair<Integer, Integer>> list = new ArrayList<>();
                Pair<Integer, Integer>[] _list;

                for (int i=0;i<path.size();i++){

                    String room = path.get(i);
                    Paint colour_of_dot ;

                    if(i==path.size()-1)
                        colour_of_dot=startPaint;
                    else
                        colour_of_dot=paint;

                    Pair<Integer, Integer> pixels = getPixels(room);


                    if(_from.equals("Reception")){
                        if((_to.equals("Ward 5") || _to.equals("Office 1") || _to.equals("Office 2") ||_to.equals("ICU 1") ||
                            _to.equals("ICU 2") ||_to.equals("ICU 3") ||_to.equals("ICU 4") ||_to.equals("Ward 4") )) {
                            if (list.isEmpty()) {
                                tempCanvas.drawCircle(175, 980, 25, colour_of_dot);
                                if (room.equals("Reception")) {
                                    px = pixels.getValue0();
                                    py = pixels.getValue1();
                                    tempCanvas.drawCircle(px, py, 25, colour_of_dot);
                                    list.add(pixels);
                                    list.add(new Pair(175, 980));

                                }
                            }
                        }
                        else {
                            px = pixels.getValue0();
                            py = pixels.getValue1();
                            tempCanvas.drawCircle(px, py, 25, colour_of_dot);
                            list.add(pixels);
                        }
                    }

                    if (!room.equals("Reception")){
                        px = pixels.getValue0();
                        py = pixels.getValue1();
                        tempCanvas.drawCircle(px, py, 25, colour_of_dot);
                        list.add(pixels);
                    }
                }

                if(_to.equals("Reception") && path.isEmpty()==false){
                    boolean contain = false;
                    if (path.contains("Office 1 ") || path.contains("Office 2") || path.contains("ICU 1") || path.contains("ICU 2") &&
                            path.contains("ICU 3") || path.contains("ICU 4") || path.contains("Ward 5") || path.contains("Ward 4")){
                        tempCanvas.drawCircle(175, 980, 25, startPaint);  //Corner dot
                        contain=true;
                    }

                    tempCanvas.drawCircle(175,1790, 25, startPaint);  //Reception dot
                    if (contain)
                        list.add(new Pair(175, 980));
                    list.add(new Pair(175, 1790));
                }

                int x0,y0 = 0;
                int x1,y1 = 0;

                for (int i=0;i<list.size();i++){
                    if(!(i==list.size()-1)){
                        Pair<Integer,Integer> p0 = list.get(i);
                        Pair<Integer,Integer>  p1 = list.get(i+1);

                        x0=p0.getValue0();
                        y0=p0.getValue1();
                        x1=p1.getValue0();
                        y1=p1.getValue1();

                        tempCanvas.drawLine(x0, y0, x1, y1, paint);

                    }
                }
                //Attach the canvas to the ImageView
                image.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
        });
    }
}


