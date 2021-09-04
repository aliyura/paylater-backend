package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.creditregistry.CRCBVNSearchRequest;
import com.syrol.paylater.services.CRCCreditBureauService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class BVNValidationController {

    private final CRCCreditBureauService crcCreditBureauService;

    @PostMapping("/bvn/validation")
    public APIResponse<String> validateVN(@Valid @RequestBody CRCBVNSearchRequest request) {
        APIResponse response= crcCreditBureauService.searchBVN(request);
        if(response.isSuccess())
            response.setMessage("Validation Successful");
        return  response;
    }

}
