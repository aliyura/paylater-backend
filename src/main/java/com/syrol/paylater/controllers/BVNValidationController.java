package com.syrol.paylater.controllers;

import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.CRCBVNSearchRequest;
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
        return crcCreditBureauService.searchBVN(request);
    }

}
