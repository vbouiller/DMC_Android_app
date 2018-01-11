package fr.emse.com.environmentcontrol.networking;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.emse.com.environmentcontrol.ContextManagementActivity;
import fr.emse.com.environmentcontrol.Model.RoomContextState;
import fr.emse.com.environmentcontrol.Model.Status;

/**
 * Created by Valentin on 13/12/2017.
 */

public class RoomContextHttpManager {

    static String CONTEXT_SERVER_URL = "https://pure-basin-20770.herokuapp.com/api/rooms/";
    static String SWITCH_LIGHT_SUFFIX = "/switch/light";
    static String SWITCH_RINGER_SUFFIX = "/switch/ringer";

    private static RequestQueue queue = null;
    public String room;


    public static void retrieveRoomContextState(String room, final ContextManagementActivity contextManagementActivity) {
        String url = CONTEXT_SERVER_URL + room + "/";

        if(queue == null){
            queue = Volley.newRequestQueue(contextManagementActivity.getApplicationContext());
            queue.start();
        }

        //get room sensed context
        JsonObjectRequest contextRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String id = response.getString("id").toString();
                            int lightLevel = Integer.parseInt(response.getJSONObject("light").get("level").toString());
                            Status lightStatus = Status.valueOf(response.getJSONObject("light").get("status").toString());
                            int noiseLevel = Integer.parseInt(response.getJSONObject("noise").get("level").toString());
                            Status noiseStatus = Status.valueOf(response.getJSONObject("noise").get("status").toString());

                            // do something with results...
                            // notify main activity for update...

                            RoomContextState roomContextState = new RoomContextState(id, lightStatus, noiseStatus, lightLevel, noiseLevel);
                            contextManagementActivity.onUpdate(roomContextState);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        queue.add(contextRequest);

    }

    public static void switchLight(final RoomContextState state, final ContextManagementActivity contextManagementActivity){

        if(queue == null){
            queue = Volley.newRequestQueue(contextManagementActivity.getApplicationContext());
            queue.start();

        }

        String url = CONTEXT_SERVER_URL + state.getRoom() + SWITCH_LIGHT_SUFFIX;

        //switch room's light
        JsonArrayRequest contextRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        retrieveRoomContextState(state.getRoom(), contextManagementActivity);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();// Some error to access URL : Room may not exists...
                    }
                });
        queue.add(contextRequest);
        //retrieveRoomContextState(state.getRoom(), contextManagementActivity);
    }

    public static void switchRoomRinger(final RoomContextState state, final ContextManagementActivity contextManagementActivity){
        if(queue == null){
            queue = Volley.newRequestQueue(contextManagementActivity.getApplicationContext());
            queue.start();
        }

        String url = CONTEXT_SERVER_URL + state.getRoom() + SWITCH_RINGER_SUFFIX;

        //switch room's light
        JsonArrayRequest contextRequest = new JsonArrayRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            retrieveRoomContextState(state.getRoom(), contextManagementActivity);
                        } catch (RuntimeException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();// Some error to access URL : Room may not exists...
                    }
                });
        queue.add(contextRequest);
        //retrieveRoomContextState(state.getRoom(), contextManagementActivity);

    }

}
