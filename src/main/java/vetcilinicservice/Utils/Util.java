//--------------------------------------- Project Information - Start ---------------------------------------//
/*
    Name: Eymen ERUYAR
    Project Name: Vet Klinik
    Page: Util Page
    Version: v1.0
    GitHub: https://github.com/eymeneruyar
    Date: 18.09.2021
*/
//--------------------------------------- Project Information - End -----------------------------------------//
package vetcilinicservice.Utils;

import org.apache.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Util {

    public static final String UPLOAD_DIR = "src/main/resources/static/uploadImages/";
    public static final int pageSize = 5;

    public static void logger(String data,Class logClass){
        Logger.getLogger(logClass).error(data);
    }

    public static boolean isEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public static List<Map<String,String>> errors(BindingResult bindingResult){

        List<Map<String,String>> ls = new LinkedList<>();

        bindingResult.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String fieldMessage = error.getDefaultMessage();

            Map<String,String> map = new HashMap<>();
            map.put("fieldName",fieldName);
            map.put("fieldMessage",fieldMessage);
            ls.add(map);

        });

        return ls;

    }

}
