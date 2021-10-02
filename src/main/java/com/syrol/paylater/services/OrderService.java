package com.syrol.paylater.services;
import com.syrol.paylater.entities.Order;
import com.syrol.paylater.entities.OrderItem;
import com.syrol.paylater.entities.User;
import com.syrol.paylater.enums.OrderDeliveryMethod;
import com.syrol.paylater.enums.OrderPaymentMethod;
import com.syrol.paylater.enums.Status;
import com.syrol.paylater.pojos.APIResponse;
import com.syrol.paylater.pojos.OrderActivationRequest;
import com.syrol.paylater.pojos.OrderCancellationRequest;
import com.syrol.paylater.pojos.paystack.PaymentInitializeRequest;
import com.syrol.paylater.pojos.paystack.PaymentVerificationResponse;
import com.syrol.paylater.pojos.zoho.*;
import com.syrol.paylater.repositories.OrderItemRepository;
import com.syrol.paylater.repositories.OrderRepository;
import com.syrol.paylater.retrofitservices.ZohoOrderServiceInterface;
import com.syrol.paylater.util.App;
import com.syrol.paylater.util.AuthDetails;
import com.syrol.paylater.util.UnsafeOkHttpClient;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final App app;
    private final AuthDetails authDetails;
    private final com.syrol.paylater.util.Response apiResponse;
    private final OrderRepository orderRepository;
    private  final OrderItemRepository orderItemRepository;
    private  final  PaymentService paymentService;
    private final  ZohoAuthService zohoAuthService;
    private ZohoOrderServiceInterface zohoOrderServiceInterface;
    private OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();
    @Value("${spring.zoho.baseURL}")
    private String baseURL;
    @Value("${spring.zoho.organization}")
    private String organization;
    @Value("${spring.order.deliveryFee}")
    private int deliveryFee;

    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder().client(okHttpClient).addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();
        zohoOrderServiceInterface = retrofit.create(ZohoOrderServiceInterface.class);
    }


    public APIResponse initiateOrder(Principal principal, ZohoOrderRequest request) {
        try {
            User user =authDetails.getAuthorizedUser(principal);
            if(request.getPaymentMethod()==null)
                return  apiResponse.failure("Payment Method Required <paymentMethod>!");
            else if(request.getShippingAddress()==null)
                return  apiResponse.failure("Shipping Address Required <shippingAddress>!");
            else if(request.getDeliveryMethod()==null)
                return  apiResponse.failure("Delivery Method Required <deliveryMethod>!");
            else if(request.getItems()==null || request.getItems().isEmpty())
                return  apiResponse.failure("Order Items Required <Array<items>>!");
            else{
                app.print("#########Initiate Order Request");
                app.print(request);
//                String authorization = String.format("Bearer %s", zohoAuthService.authManager().getAccess_token());

                if(request.getEmail()==null)
                    request.setEmail(user.getEmail());
                if(request.getCountry()==null)
                    request.setCountry(user.getCountry());
                if(request.getCity()==null)
                    request.setCity(user.getCity());
                if(request.getName()==null)
                    request.setName(user.getName());
                if(request.getEmail()==null)
                    request.setEmail(user.getEmail());
                if(request.getPhoneNumber()==null)
                    request.setPhoneNumber(user.getMobile());

                //get total order sum
                double totalAmount=0;
                for (OrderItem item: request.getItems()) {
                    if(item.getQuantity()<=0)
                        item.setQuantity(1);
                        totalAmount += item.getRate() * item.getQuantity();
                }

                if(totalAmount>0) {

                    //set delivery fee to zero if home pickup
                    if (request.getDeliveryMethod().equals(OrderDeliveryMethod.HOME_DELIVERY))
                        deliveryFee = 500;

                    Order orderRequest=app.getMapper().convertValue(request,Order.class);
                    totalAmount = totalAmount + deliveryFee;
                    orderRequest.setAmount(totalAmount);
                    orderRequest.setOrderReference(app.generateRandomId());
                    orderRequest.setDeliveryFee(deliveryFee);
                    orderRequest.setContactId(user.getContactId());
                    orderRequest.setLastModifiedDate(new Date());
                    orderRequest.setCreatedDate(new Date());
                    orderRequest.setStatus(Status.PP);
                    orderRequest.setUuid(user.getUuid());

                    if (orderRequest.getPaymentMethod().equals(OrderPaymentMethod.PAYNOW)) {
                        PaymentInitializeRequest paymentRequest = new PaymentInitializeRequest();
                        paymentRequest.setEmail(user.getEmail());
                        paymentRequest.setReference(orderRequest.getOrderReference());
                        paymentRequest.setAmount(orderRequest.getAmount());
                        paymentRequest.setCallback_url("https://paylater.com/payment/status");
                        APIResponse<PaymentVerificationResponse> paymentInitiateResponse=   paymentService.initializePayment(principal, paymentRequest, orderRequest.getOrderReference());
                        if(paymentInitiateResponse.isSuccess()){
                              //save order
                               Order savedOrder= orderRepository.save(orderRequest);
                                for (OrderItem item: request.getItems()) {
                                    item.setOrderId(savedOrder.getId());
                                    item.setCreatedDate(new Date());
                                }
                                orderItemRepository.saveAll(request.getItems());

                              return apiResponse.success(paymentInitiateResponse.getPayload());
                        }else{
                            return paymentInitiateResponse;
                        }
                    }
                    else if(request.getPaymentMethod().equals(OrderPaymentMethod.POD)){
                        orderRequest.setStatus(Status.PC);
                        orderRequest.setStatusReason("Pay on delivery order");
                        //save order
                        Order savedOrder= orderRepository.save(orderRequest);
                        for (OrderItem item: request.getItems()) {
                            item.setOrderId(savedOrder.getId());
                            item.setCreatedDate(new Date());
                        }
                        orderItemRepository.saveAll(request.getItems());

                        return apiResponse.success(savedOrder);
                    }
                    else if(request.getPaymentMethod().equals(OrderPaymentMethod.PAYLATER)){
                        orderRequest.setStatus(Status.PC);
                        orderRequest.setStatusReason("Pay latter order");
                        //save order
                        Order savedOrder= orderRepository.save(orderRequest);
                        for (OrderItem item: request.getItems()) {
                            item.setOrderId(savedOrder.getId());
                            item.setCreatedDate(new Date());
                        }
                        orderItemRepository.saveAll(request.getItems());

                        return apiResponse.success(savedOrder);
                    }else{
                        return apiResponse.failure("Invalid Payment Method <paymentMethod>");
                    }
                }else{
                    return  apiResponse.failure("Total Order Amount Can't be Zero");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }

    public APIResponse updateOrder(Principal principal, String orderReference, OrderActivationRequest request) {
        try {
            User user = authDetails.getAuthorizedUser(principal);

            if (request.getPaidAmount() == null)
                apiResponse.failure("Paid Amount Required");

            app.print("#########Zoho Update Order Request");
            app.print(request);
            String authorization = String.format("Bearer %s", zohoAuthService.authManager().getAccess_token());

            Order userOrder = orderRepository.findByOrderReference(orderReference).orElse(null);
            if (userOrder != null) {

                if (userOrder.getPaymentMethod().equals(OrderPaymentMethod.PAYNOW)) {
                    if (request.getPaymentReference() != null) {
                        userOrder.setStatusReason(request.getRemark());
                        userOrder.setPaymentReference(request.getPaymentReference());
                        userOrder.setClearedAmount(request.getPaidAmount());
                        userOrder.setStatus(Status.AC);
                        userOrder.setPaymentDate(new Date());
                        userOrder.setLastModifiedDate(new Date());
                        userOrder.setLastModifiedDate(new Date());
                        return apiResponse.success(orderRepository.save(userOrder));
                    } else {
                       return apiResponse.failure("Invalid Payment Reference");
                    }
                } else if (userOrder.getPaymentMethod().equals(OrderPaymentMethod.POD)) {
                    userOrder.setPaymentReference(request.getPaymentReference());
                    userOrder.setStatusReason(request.getRemark());
                    userOrder.setClearedAmount(request.getPaidAmount());
                    userOrder.setStatus(Status.AC);
                    userOrder.setPaymentDate(new Date());
                    userOrder.setLastModifiedDate(new Date());
                    userOrder.setLastModifiedDate(new Date());
                    return apiResponse.success(orderRepository.save(userOrder));
                } else if (userOrder.getPaymentMethod().equals(OrderPaymentMethod.PAYLATER)) {
                    userOrder.setPaymentReference(request.getPaymentReference());
                    userOrder.setStatusReason(request.getRemark());
                    userOrder.setClearedAmount(userOrder.getClearedAmount() + request.getPaidAmount());
                    userOrder.setPaymentDate(new Date());
                    userOrder.setLastModifiedDate(new Date());
                    userOrder.setLastModifiedDate(new Date());
                    //activate order if all amount cleared
                    if (userOrder.getClearedAmount().equals(userOrder.getAmount()))
                        userOrder.setStatus(Status.AC);
                    else
                        userOrder.setStatus(Status.PA);
                    return apiResponse.success(orderRepository.save(userOrder));
                } else {
                   return apiResponse.failure("Unable to Activate Order!");
                }
            } else {
                return apiResponse.failure("Invalid Order or not Initiated!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }


    public APIResponse cancelOrder(Principal principal, String orderReference, OrderCancellationRequest request) {
        try {
            User user = authDetails.getAuthorizedUser(principal);

            if (request.getReason() == null)
                apiResponse.failure("Cancellation Reason Required!");

            app.print("######### Cancel Order Request");
            app.print(request);
            String authorization = String.format("Bearer %s", zohoAuthService.authManager().getAccess_token());

            Order userOrder = orderRepository.findByOrderReference(orderReference).orElse(null);
            if (userOrder != null) {
                userOrder.setStatusReason(request.getReason());
                userOrder.setStatus(Status.CA);
                userOrder.setLastModifiedDate(new Date());
                userOrder.setLastModifiedDate(new Date());
                return apiResponse.success(orderRepository.save(userOrder));
            } else {
                return apiResponse.failure("Invalid Order or not Initiated!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return apiResponse.failure(ex.getMessage());
        }
    }
    public APIResponse<Order> getOrder(Principal principal, String orderReference) {
        Order order = orderRepository.findByOrderReference(orderReference).orElse(null);
        if(order!=null)
          return apiResponse.success(order);
        else
            return  apiResponse.failure("Order not Found");
    }

    public APIResponse<Order> getMyOrders(Principal principal) {
        User currentUser=authDetails.getAuthorizedUser(principal);
        List<Order> order = orderRepository.findByUuid(currentUser.getUuid()).orElse(null);
        if(order!=null)
            return apiResponse.success(order);
        else
            return  apiResponse.failure("No Order Available");
    }
    public APIResponse<Order> getOrders(Principal principal, Pageable pageable) {
        Page<Order> orderPage =  orderRepository.findAll(pageable);
        if(orderPage!=null)
            return apiResponse.success(orderPage);
        else
            return  apiResponse.failure("No Order Available");
    }

    public APIResponse<Order> getOrderItems(Principal principal, Long orderId) {
        Order order =  orderRepository.findById(orderId).orElse(null);
        if(order!=null)
            return apiResponse.success(orderItemRepository.findByOrderId(orderId));
        else
            return  apiResponse.failure("No Order Available");
    }




    public APIResponse<Order> getActiveOrders(Principal principal, Pageable pageable) {
         Page<Order> orderPage=orderRepository.findByStatus(Status.AC, pageable).orElse(null);
         if(orderPage!=null)
             return apiResponse.success(orderPage);
         else
            return apiResponse.fail("No Active Order Found");
    }

    public APIResponse<Order> getCanceledOrders(Principal principal, Pageable pageable) {
        Page<Order> orderPage=orderRepository.findByStatus(Status.CA, pageable).orElse(null);
        if(orderPage!=null)
            return apiResponse.success(orderPage);
        else
            return apiResponse.fail("No Canceled Order Found");
    }

    public APIResponse<Order> getPaylaterOrders(Principal principal, Pageable pageable) {
        Page<Order> orderPage=orderRepository.findByPaymentMethod(OrderPaymentMethod.PAYLATER, pageable).orElse(null);
        if(orderPage!=null)
            return apiResponse.success(orderPage);
        else
            return apiResponse.fail("No Paylater Order Found");
    }

    @GetMapping("/order/get_pod_orders")
    public APIResponse<Order> getPodOrders(Principal principal, Pageable pageable) {
        Page<Order> orderPage=orderRepository.findByPaymentMethod(OrderPaymentMethod.POD, pageable).orElse(null);
        if(orderPage!=null)
            return apiResponse.success(orderPage);
        else
            return apiResponse.fail("No Pay on Delivery Order Found");
    }

    public APIResponse<Order> getPaynowOrders(Principal principal, Pageable pageable) {
        Page<Order> orderPage=orderRepository.findByPaymentMethod(OrderPaymentMethod.PAYNOW, pageable).orElse(null);
        if(orderPage!=null)
            return apiResponse.success(orderPage);
        else
            return apiResponse.fail("No Pay Online Order Found");
    }

}