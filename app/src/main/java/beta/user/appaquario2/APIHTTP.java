package beta.user.appaquario2;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by User on 19/09/2017.
 */

public class APIHTTP {
    public static JSONObject getObject(String param) throws ErroAPI, JSONException, IOException {
        StringBuilder retorno = Connect(param);
        hasErro(retorno);
        return  new JSONObject(retorno.toString());
    }

    public static JSONArray getArray(String param) throws JSONException, ErroAPI, IOException {
        StringBuilder retorno = Connect(param);
        hasErro(retorno);
        return new JSONArray(retorno.toString());
    }

    private static void hasErro(StringBuilder str) throws JSONException, ErroAPI {
        Object json = new JSONTokener(str.toString()).nextValue();
        if (json instanceof JSONObject) {
            JSONObject jsonObj = new JSONObject(str.toString());
            if (jsonObj.has("erro")){
                throw new ErroAPI(jsonObj.getString("erro"), jsonObj.getString("cd_erro"));
            }
        }
    }

    private static StringBuilder Connect(String param) throws IOException {
        HttpURLConnection connection;
        StringBuilder sb = new StringBuilder();
            URL url = new URL("http://www.vssistemas.com.br/arduino_query.php");

            byte[] postData = param.getBytes(StandardCharsets.UTF_8);

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

        Log.i("API",sb.toString());
        return sb;
    }

    static class  ErroAPI extends Exception {
        private String msg;
        public ErroAPI(String msg, String id){
            super(msg);
            this.msg = "CD - "+id+": "+msg;
        }
        public String getMessage(){
            return msg;
        }
    }

}
