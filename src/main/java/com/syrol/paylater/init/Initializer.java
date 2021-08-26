package com.syrol.paylater.init;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Initializer {

    @Autowired
    private final App app;
    @Autowired
    private final Response response;
    @Autowired
    private final ResourceReader reader;

    public APIResponse getInitialized(String target) {
        List data = reader.read(target);
        if (data != null)
            return response.success(data);
        else
            return response.failure("Unable to read target " + target);
    }
}