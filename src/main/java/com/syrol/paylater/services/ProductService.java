package com.syrol.paylater.services;
import com.syrol.paylater.entities.Product;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.UpdateProductRequest;
import com.syrol.paylater.repositories.ProductRepository;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements Serializable {

    private final  App app;
    private final Response response;
    private final AuthDetails authDetails;
    private final ProductRepository productRepository;
    private final  S3BucketStorageService s3BucketStorageService;


    public APIResponse createProduct(Principal principal, Product product){

        User user= authDetails.getAuthorizedUser(principal);
        if (product.getName() == null)
            return response.failure("Product Name Required <name>!");
        else if (product.getDescription() == null)
            return response.failure("Product Description Required <description>!");
        else if (product.getItemId() == null)
            return response.failure("Item ID Required <itemId>!");
        else if (product.getRate() == null)
            return response.failure("Product Rate Required <rate>!");
        else {

            app.print("Add Product Request:");
            app.print(product);
            Product existingProduct=productRepository.findByItemId(product.getItemId()).orElse(null);
            if(existingProduct==null){
                product.setCreatedDate(new Date());
                product.setStatus(Status.AC);
                product.setCreatedBy(user.getUuid());
                product.setCreatedByUsername(user.getEmail());
                product.setLastModifiedDate(new Date());
                return response.success(  productRepository.save(product));
            }else{
                return  response.failure("Product ["+product.getItemId()+"] already exist");
            }
        }
    }
    public APIResponse updateProduct(Principal principal, String itemId, UpdateProductRequest product) {

        if (itemId == null)
            return response.failure("Item ID Required <itemId>!");
        else {
            User user= authDetails.getAuthorizedUser(principal);
            app.print("Add Product Request:");
            app.print(product);
            Product existingProduct=productRepository.findByItemId(itemId).orElse(null);
            if(existingProduct!=null){

                if (product.getName() != null)
                     existingProduct.setName(product.getName());
                if (product.getDescription() != null)
                    existingProduct.setDescription(product.getDescription());
                if (product.getStatus() != null)
                    existingProduct.setStatus(product.getStatus());
                if (product.getRate() == null)
                    existingProduct.setRate(product.getRate());
                if (product.getProductType() == null)
                    existingProduct.setProductType(product.getProductType());

                existingProduct.setCreatedDate(new Date());
                existingProduct.setCreatedBy(user.getUuid());
                existingProduct.setCreatedByUsername(user.getEmail());
                existingProduct.setLastModifiedDate(new Date());

                return response.success(productRepository.save(existingProduct));

            }else{
                return  response.failure("Product ["+itemId+"] doesn't exist");
            }
        }
    }

    public APIResponse activateProduct(String itemId) {
        Product existingProduct=productRepository.findByItemId(itemId).orElse(null);
        if(existingProduct!=null) {
            existingProduct.setStatus(Status.AC);
            existingProduct.setLastModifiedDate(new Date());
             return  response.success(productRepository.save(existingProduct));
        }else{
            return  response.failure("Product ["+itemId+"] doesn't exist");
        }
    }

    public APIResponse deActivateProduct(String itemId) {
        Product existingProduct=productRepository.findByItemId(itemId).orElse(null);
        if(existingProduct!=null) {
            existingProduct.setStatus(Status.IA);
            existingProduct.setLastModifiedDate(new Date());
            return  response.success(productRepository.save(existingProduct));
        }else{
            return  response.failure("Product ["+itemId+"] doesn't exist");
        }
    }

    public APIResponse addThumbnail(Principal principal,String itemId, MultipartFile thumbnail) {
        Product existingProduct = productRepository.findByItemId(itemId).orElse(null);
        if (existingProduct != null) {

            if (app.validImage(thumbnail.getOriginalFilename())) {
                String fileName = "product" + existingProduct.getVendorId() + "" + app.makeUIID() + ".jpg";
                APIResponse response1 = s3BucketStorageService.uploadFile(fileName, thumbnail);
                app.print(response1);
                if (response1.isSuccess()) {
                    existingProduct.setStatus(Status.IA);
                    existingProduct.setLastModifiedDate(new Date());
                    existingProduct.setThumbnail(response1.getPayload().toString());
                    return response.success(productRepository.save(existingProduct));
                } else
                    return response.failure(response1.getMessage());
            } else {
                return response.failure("Thumbnail file format not supported");
            }
        }else{
            return response.failure("Product [" + itemId + "] doesn't exist");
        }
    }

    public APIResponse addImages(Principal principal,String itemId, MultipartFile[] images) {
        Product existingProduct=productRepository.findByItemId(itemId).orElse(null);
        if(existingProduct!=null) {
            ArrayList<String> uploadedProducts = new ArrayList();
            for (MultipartFile file : images) {
                String itemFileName="product"+existingProduct.getVendorId()+""+app.makeUIID()+".jpg";
                if (file != null) {
                    if (app.validImage(file.getOriginalFilename())) {
                        APIResponse response2 = s3BucketStorageService.uploadFile(itemFileName,file);
                        app.print(response2);
                        if (response2.isSuccess())
                            uploadedProducts.add(response2.getPayload().toString());
                        else
                            app.print(response2);
                    }
                }
            }
            //join the uploaded images
            String productImages = "";
            for (String name : uploadedProducts) {
                productImages += name + ",";
            }
            //remove last comman in the string
            if (productImages.endsWith(",")) {
                productImages = productImages.substring(0, productImages.length() - 1);
            }
            //set product images to thumbnail if no image uploaded
            if (productImages != null && !productImages.isEmpty()) {
                existingProduct.setImages(productImages);
            }

            existingProduct.setStatus(Status.IA);
            existingProduct.setLastModifiedDate(new Date());
            return  response.success(productRepository.save(existingProduct));
        }else{
            return  response.failure("Product ["+itemId+"] doesn't exist");
        }
    }


    public APIResponse deleteById(Long id) {
        productRepository.deleteById(id);
        return response.success("Deleted Successfully");
    }

    public APIResponse deleteByItemId(String itemId) {
        try {
            productRepository.deleteByItemId(itemId);
            return response.success("Deleted Successfully");
        } catch (Exception ex) {
            return response.failure("Invalid Item Id");
        }
    }

    public APIResponse findById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product!=null)
            return response.success(product);
        else
            return response.failure("Product not Found");
    }

    public APIResponse findByItemId(String  itemId) {
        Product product = productRepository.findByItemId(itemId).orElse(null);
        if (product != null)
            return response.success(product);
        else
            return response.failure("Product not found");
    }

    public APIResponse findByStatus(Status status) {
        List<Product> products = productRepository.findByStatus(status).orElse(null);
        if (!products.isEmpty())
            return response.success(products);
        else
            return response.failure("No Product Found");
    }

    public APIResponse findBySearch(String search, Pageable pageable) {
        Page<Product> products = productRepository.findAllBySearch(pageable,search);
        if (!products.isEmpty())
            return response.success(products);
        else
            return response.failure("Search not Found");
    }

    public APIResponse findAllByType(Pageable pageable, String productType) {
        Page<Product> productPage = productRepository.findAllByProductType(productType,pageable).orElse(null);
        if (productPage!=null)
            return response.success(productPage);
        else
            return response.failure("No Product Available");
    }

    public APIResponse findAll(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        if (!productPage.isEmpty())
            return response.success(productPage);
        else
            return response.failure("No Product Available");
    }
}