package com.example.readguardian;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	TextView getResponse;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	getResponse = (TextView) findViewById(R.id.textView1);
	    new HttpAsyncTask().execute("http://content.guardianapis.com/search?api-key=test&page-size=5&order-by=newest");
	}
	
	public static String GET(String url){
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null) {
			    System.out.println("2a--try--");
			    result = convertInputStreamToString(inputStream);
			} else {
			    System.out.println("2b--try--");
			    result = "Did not work!";
			}
		} catch (Exception e) {
			    Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}
	
	public static String convertInputStreamToString(InputStream entityResponse) {

		InputStreamReader is = new InputStreamReader(entityResponse);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		try {
			String read = br.readLine();

			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
	public static Map<String,String> parse(JSONObject json , Map<String,String> out) throws JSONException{
	    Iterator<String> keys = json.keys();
	    while(keys.hasNext()){
	        String key = keys.next();
	        String val = null;
	        try{
	             JSONObject value = json.getJSONObject(key);
	             parse(value,out);
	        }catch(Exception e){
	            val = json.getString(key);
	        }

	        if(val != null){
	            out.put(key,val);
	        }
	    }
	    return out;
	}
	private static String convertInputStreamToString2(InputStream inputStream) throws IOException{
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	    String line = "";
	    String result = "";
	    while((line = bufferedReader.readLine()) != null)
	    	result += line;
 	   inputStream.close();
	   return result;
	}
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
		  return GET(urls[0]);
		}
		// onPostExecute displays the results of the AsyncTask.
		
		protected void onPostExecute(String result) {
			System.out.println(result.getClass().getName());
			getResponse.setText(result);
			JSONObject jObject;
			try {
				jObject = new JSONObject(result);
				JSONObject jObject2 = jObject.getJSONObject("response");
				JSONArray jArray = jObject2.getJSONArray("results");
				for (int i = 0; i < jArray.length(); i++) {
					Map<String,String> out = new HashMap<String, String>();
					out = parse((JSONObject)jArray.get(i),out);
					Iterator it = out .entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pairs = (Map.Entry)it.next();
				        System.out.println(pairs.getKey() + " = " + pairs.getValue());
				        it.remove(); // avoids a ConcurrentModificationException
				    }
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    }
	

}