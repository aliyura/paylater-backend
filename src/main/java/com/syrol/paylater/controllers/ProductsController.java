package com.syrol.paylater.controllers;

import com.syrol.paylater.entities.Product;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.UpdateProductRequest;
import com.syrol.paylater.pojos.zoho.ZohoItemRequest;
import com.syrol.paylater.pojos.zoho.ZohoMultiItemResponse;
import com.syrol.paylater.pojos.zoho.ZohoResponse;
import com.syrol.paylater.pojos.zoho.ZohoSingleItemResponse;
import com.syrol.paylater.repositories.ProductRepository;
import com.syrol.paylater.services.ProductService;
import com.syrol.paylater.services.ZohoItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.http.DELETE;
import retrofit2.http.PUT;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping(path = "api/v1/")
public class ProductsController {

    private final ProductService productService;

    @PostMapping("/product/add")
    public APIResponse<Product> addProduct(Principal principal, @RequestBody Product request) {
        return productService.createProduct(principal,request);
    }

    @PostMapping(value="/product/thumbnail/add/{itemId}", consumes = "multipart/form-data")
    public APIResponse<Product> addProductThumbnail(Principal principal, @PathVariable String itemId, @RequestParam( value = "thumbnail") MultipartFile file) {
        return productService.addThumbnail(principal,itemId, file);
    }

    @PostMapping(value="/product/images/add/{itemId}", consumes = "multipart/form-data")
    public APIResponse<Product> addProductThumbnail(Principal principal, @PathVariable String itemId, @RequestParam( value = "images")  MultipartFile[] images) {
        return productService.addImages(principal,itemId, images);
    }

    @PostMapping("/product/images/add/{itemId}")
    public APIResponse<Product> addProductImages(Principal principal, @PathVariable String itemId, @RequestBody Product request) {
        return productService.createProduct(principal,request);
    }

    @PUT("/product/update/{itemId}")
    public APIResponse<Product> updateProduct(Principal principal, @PathVariable String itemId, @RequestBody UpdateProductRequest request) {
        return productService.updateProduct(principal,itemId,request);
    }

    @PUT("/product/activate/{itemId}")
    public APIResponse<Product> activateItem(@PathVariable String itemId) {
        return productService.activateProduct(itemId);
    }

    @PUT("/product/deactivate/{itemId}")
    public APIResponse<Product> deActivateItem(@PathVariable String itemId) {
        return productService.deActivateProduct(itemId);
    }

    @DELETE("/product/delete/{itemId}")
    public APIResponse<Product> deleteItem(@PathVariable String itemId) {
        return productService.deleteByItemId(itemId);
    }

    @GetMapping("/product/{itemId}")
    public APIResponse<Product> getItem(@PathVariable String itemId) {
        return productService.findByItemId(itemId);
    }

    @GetMapping("/product/search")
    public APIResponse<Product> getItems(@RequestParam int page, @RequestParam int size, @RequestParam String q) {
        return productService.findBySearch(q, PageRequest.of(page,size));
    }
    @GetMapping("/products")
    public APIResponse<Product> getItems( @RequestParam int page, @RequestParam int size) {
        return productService.findAll(PageRequest.of(page,size));
    }
}
