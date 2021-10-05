package com.syrol.paylater.pojos.mono;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoDirectDebitStatusResponseData<T> {
  private String _id;
   private String id;
  private String status;
  private Double  amount;
  private String description;
  private Double fee;
  private String currency;
  private String account;
  private String  customer;
  private String reference;
  private String created_at;
  private String updated_at;
}
