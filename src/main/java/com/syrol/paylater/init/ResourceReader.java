package com.syrol.paylater.init;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ResourceReader {
        List read(String target) {
            JSONParser jsonParser = new JSONParser();
            try {
                File resource = new ClassPathResource(target + ".json").getFile();
                if(resource!=null) {
                    FileReader reader = new FileReader(String.valueOf(resource.toPath()));
                    ArrayList list =(ArrayList) jsonParser.parse(reader);
                    return list;
                }else{
                    return  null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
}
