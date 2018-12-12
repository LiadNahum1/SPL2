package bgu.spl.mics.application;



import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;




/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        Gson gson = new Gson();

        try {

            Object obj = parser.parse(new FileReader(args[0]));
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            // loop array
            JSONArray inventoryObj = (JSONArray) jsonObject.get("initialInventory");
            Iterator<String> iterator = inventoryObj.iterator();
            while (iterator.hasNext()) {
                BookInventoryInfo book = gson.fromJson(iterator.next(), BookInventoryInfo.class);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}


