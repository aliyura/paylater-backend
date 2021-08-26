package com.syrol.paylater.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.syrol.paylater.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class App {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void log(String message) {
        logger.info(message);
    }
    public void print(Object obj){
        try {
            logger.info(new ObjectMapper().writeValueAsString(obj));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public  long generateSerialNumber(int prefix) {
        Random rand = new Random();
        long x = (long)(rand.nextDouble()*100000000000000L);
        String s = prefix + String.format("%014d", x);
        return Long.valueOf(s);
    }

    public String makeUIID() {
        UUID timebaseUUID = Generators.timeBasedGenerator().generate();
        return timebaseUUID.toString();
    }
    public boolean validImage(String fileName)
    {
        String regex = "(.*/)*.+\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP|JPEG)$";
        Pattern p = Pattern.compile(regex);
        if (fileName == null) {
            return false;
        }
        Matcher m = p.matcher(fileName);
        return m.matches();
    }

    public boolean validEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean validNumber(String number) {
        if (number.startsWith("+234"))
           number= number.replace("+234", "0");
        Pattern pattern = Pattern.compile("^\\d{11}$");
        Matcher matcher = pattern.matcher(number);
        return matcher.matches();
    }

    public Long generateOTP(){
        Random rnd = new Random();
        Long number = Long.valueOf(rnd.nextInt(999999));
        return  number;
    }
}
