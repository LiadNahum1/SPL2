package bgu.spl.mics.application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        try(Reader reader = new InputStreamReader(new FileInputStream("input.json"), "UTF-8")){
            Gson gson = new GsonBuilder().create();
            InputClass input = gson.fromJson(reader, InputClass.class);
            System.out.println(input.getServices().getTime().getSpeed());
        }
        catch(Exception e){}
    }
}


