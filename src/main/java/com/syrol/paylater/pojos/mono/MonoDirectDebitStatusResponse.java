package com.syrol.paylater.pojos.mono;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoDirectDebitStatusResponse<T> {
  private String message;
  private MonoDirectDebitStatusResponseData data;
}
