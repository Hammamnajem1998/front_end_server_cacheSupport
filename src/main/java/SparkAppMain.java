import org.apache.log4j.BasicConfigurator;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SparkAppMain {
    private static JSONArray booksCache=new JSONArray();
    private static boolean firstSearch=true;
    public static void main(String[] args) {
        BasicConfigurator.configure();
        get("/search/:value", (req, res) ->{

            String output1 = "";
            boolean foundedOnCache=false;
            JSONObject temp= new JSONObject();
            JSONArray temp1 =new JSONArray();
            if (booksCache.length() != 0 && !firstSearch) {
                for(int i=0;i<booksCache.length();i++){
                    if(booksCache.getJSONObject(i).get("topic").toString().equals(req.params(":value"))){
                        output1+="<pre>-title: "+booksCache.getJSONObject(i).get("title")+".       ID: "+booksCache.getJSONObject(i).get("id")+".<pre>";
                        foundedOnCache=true;
                    }
                }
            }
            if (!foundedOnCache)
            {

                try {
                    String topic = req.params(":value");
                    topic = topic.replaceAll(" ", "%20");
                    URL url = new URL("http://192.168.7.102:4567/search/" + topic);//put URL and port number related to your machine
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        output1 += output;
                    }
                    conn.disconnect();

                } catch (IOException e) {

                    e.printStackTrace();
                }
                temp =new JSONObject(output1);
                temp1 = temp.getJSONArray("contains");
                output1="";
                for (int i=0; i<temp1.length(); i++)
                {
                    if (temp1.getJSONObject(i).get("topic").toString().equals(req.params(":value"))){
                        booksCache.put(temp1.getJSONObject(i));
                        output1+="<pre>-title: "+temp1.getJSONObject(i).get("title")+".       ID: "+temp1.getJSONObject(i).get("id")+".<pre>";
                    }
                }
            }
            firstSearch=false;
            return output1;
        });

        get("/lookup/:value", (req, res) ->{
            String output1 ="";
            boolean foundedOnCache=false;
            JSONObject temp=new JSONObject();
            if (booksCache.length()!=0)
            {
                for(int i=0;i<booksCache.length();i++){
                    if(booksCache.getJSONObject(i).get("id").toString().equals(req.params(":value"))){
                        temp=booksCache.getJSONObject(i);
                        foundedOnCache=true;
                    }
                }
            }
            if (!foundedOnCache)
                try {
                    URL url = new URL("http://192.168.7.102:4567/lookup/"+req.params(":value"));//put URL and port number related to your machine
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));

                    String output;
                    System.out.println("Output from Server .... \n");
                    while ((output = br.readLine()) != null) {
                        output1+=output;
                    }
                    conn.disconnect();
                    temp = new JSONObject(output1);
                    booksCache.put(temp);

                } catch (IOException e) {

                    e.printStackTrace();
                }
                output1="";
                output1+="<pre> Topic: "+temp.get("topic")+".      Title: "+temp.get("title")+".         Details: "+temp.get("details")+".<pre>";

            return output1;

        });

        get("/buy/:value", (req, res) ->{
            String output1 ="";
            try {

                URL url = new URL("http://192.168.7.109:4567/buy/"+req.params(":value"));//put URL and port number related to your machine

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    output1+=output;
                }
                conn.disconnect();

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
            return output1;

        });

        get("/invalidate/:value", (req, res) ->{
            String id=req.params(":value");
            for(int i=0;i<booksCache.length();i++){
                if(booksCache.getJSONObject(i).get("id").toString().equals(id)){
                    booksCache.remove(i);
                }
            }
            return "done";
        });
    }
}
