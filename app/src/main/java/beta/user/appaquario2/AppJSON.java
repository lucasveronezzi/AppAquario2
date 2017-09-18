package beta.user.appaquario2;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AppJSON extends AsyncTask<String, Void, StringBuilder> {

    private ProgressDialog pDialog;
    private JSONArray resultArray;
    private final URL url = new URL("http://www.vssistemas.com.br/arduino_query.php");
    private Context context;

    private String result;
    private String cdError;
    private String error;
    private String funcao = "";

    public String getResult() {
        return result;
    }

    public String getCdError() {
        return cdError;
    }

    public String getError() {
        return error;
    }

    public JSONArray getResultArray() {
        return resultArray;
    }


    public AppJSON(Context context) throws MalformedURLException {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Getting Data ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    @Override
    protected StringBuilder doInBackground(String... urlString) {
        HttpURLConnection connection;
        StringBuilder sb = new StringBuilder();
        try {
            byte[] postData = urlString[0].getBytes(StandardCharsets.UTF_8);

            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", Integer.toString(postData.length));

            connection.getOutputStream().write(postData);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                connection.disconnect();
            }
            return sb;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb;
    }

    @Override
    protected void onPostExecute(StringBuilder retorno) {
        super.onPostExecute(retorno);
        pDialog.dismiss();
        System.out.println("ss "+retorno);
        try {
            if(retorno.toString() != null && retorno.toString() != "") {
                Object json = new JSONTokener(retorno.toString()).nextValue();
                if (json instanceof JSONArray) {
                    JSONArray jsonArr = new JSONArray(retorno.toString());
                    this.result = "ok";
                    resultArray = jsonArr;
                } else if (json instanceof JSONObject) {
                    JSONObject jsonObj = new JSONObject(retorno.toString());
                    if (jsonObj.has("result") && jsonObj.getString("result").toString() == "ok") {
                        this.result = jsonObj.getString("result");
                        System.out.println("ss " + result);
                    } else {
                        System.out.println("erro");
                    }
                }
            }else {
                this.result = "sem retorno";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}