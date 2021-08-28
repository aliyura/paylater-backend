package com.syrol.paylater.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.google.gson.Gson;
import com.syrol.paylater.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class App {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public HttpHeaders getHTTPHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setCacheControl("no-cache");
        return headers;
    }
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
    public String toString(Object obj){
        try {
            return new Gson().toJson(obj);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return  null;
        }
    }
    public  long generateSerialNumber(int prefix) {
        Random rand = new Random();
        long x = (long)(rand.nextDouble()*100000000000000L);
        String s = prefix + String.format("%014d", x);
        return Long.valueOf(s);
    }

    public String generateRandomNumber(int min, int max) {
        Random r = new Random();
        return String.valueOf(r.nextInt((max - min) + 10) + min);

    }
    public String generateRandomId() {
        UUID referenceId = Generators.timeBasedGenerator().generate();
        String id= referenceId.toString().replaceAll("-", "");
        return id.substring(id.length()/2);
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

    public boolean validateBVN(String number) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (number == null) {
            return false;
        }
        return pattern.matcher(number).matches() && number.length()==11;
    }


    public Long generateOTP(){
        Random rnd = new Random();
        Long number = Long.valueOf(rnd.nextInt(999999));
        return  number;
    }
    public String generateAccountNumber(){
        Random rnd = new Random();
        Long number = Long.valueOf(rnd.nextInt(99999999));
        return String.valueOf(number);
    }

}
