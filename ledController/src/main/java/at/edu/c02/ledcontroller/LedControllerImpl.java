package at.edu.c02.ledcontroller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This class handles the actual logic
 */
public class LedControllerImpl implements LedController {
    private final ApiService apiService;

    public LedControllerImpl(ApiService apiService)
    {
        this.apiService = apiService;
    }

    public ApiService getApiService()
    {
        return apiService;
    }

    @Override
    public void demo() throws IOException
    {
        // Call `getLights`, the response is a json object in the form `{ "lights": [ { ... }, { ... } ] }`
        JSONObject response = apiService.getLights();
        // get the "lights" array from the response
        JSONArray lights = response.getJSONArray("lights");

        laufLicht(6);
        // read the first json object of the lights array
        JSONObject firstLight = lights.getJSONObject(0);
        turnOffAllLeds();
        // read int and string properties of the light
        System.out.println("First light id is: " + firstLight.getInt("id"));
        System.out.println("First light color is: " + firstLight.getString("color"));
    }

    @Override
    public JSONArray getGroupLEds() throws IOException {
        JSONObject response = apiService.getLights();
        JSONArray alleLichter = response.getJSONArray("lights");
        JSONArray result  = new JSONArray();
        for(int i = 0; i < alleLichter.length(); i++){
            if(alleLichter.getJSONObject(i).getJSONObject("groupByGroup").getString("name").equals("H")){
                result.put(alleLichter.getJSONObject(i));
            }

        }
        return result;
    }
    public JSONObject turnOffAllLeds() throws IOException {
        JSONArray jsonArray = getGroupLEds();
        JSONObject jsonObject = null;
        for (int i = 0;i < jsonArray.length();i++){
            jsonObject = jsonArray.getJSONObject(i);
            jsonObject.put("state", false);
            apiService.setLed(jsonObject.getInt("id"),jsonObject.getString("color"), false);

        }
        return jsonObject;
    }
    public JSONObject laufLicht(int rounds) throws IOException {
        JSONObject jsonObject = null;
        JSONArray jsonArray = getGroupLEds();
        turnOffAllLeds();
        int counter =0;

        while (counter<rounds) {
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                jsonObject.put("state", true);
                jsonObject.put("color", "#F00");
                apiService.setLed(jsonObject.getInt("id"), "#F00", true);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                apiService.setLed(jsonObject.getInt("id"), "#F00", false);
            }
            counter++;
        }

        return jsonObject;
    }
}
