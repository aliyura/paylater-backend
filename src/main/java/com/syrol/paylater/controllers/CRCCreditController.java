package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.CRCBVNSearchRequest;
import com.syrol.paylater.services.CRCCreditBureauService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class CRCCreditController {

    private final CRCCreditBureauService crcCreditBureauService;

    @PostMapping("/crc/credit_check")
    public APIResponse<String> creditCheck(@Valid @RequestBody CRCBVNSearchRequest request) {
        APIResponse response= crcCreditBureauService.searchBVN(request);
        if(response.isSuccess())
          response.setMessage("Request Successful");
        return  response;
    }

}
