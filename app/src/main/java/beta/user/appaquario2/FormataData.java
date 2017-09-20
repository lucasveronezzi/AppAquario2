package beta.user.appaquario2;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by User on 20/09/2017.
 */

public class FormataData {
    private static SimpleDateFormat formatoMYSQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static String formatToString(String dateMysql, String formato){
        SimpleDateFormat formatDate = new SimpleDateFormat(formato);
        try {
            return formatDate.format(formatoMYSQL.parse(dateMysql));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
