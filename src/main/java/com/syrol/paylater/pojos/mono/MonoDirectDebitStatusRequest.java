package com.syrol.paylater.pojos.mono;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonoDirectDebitStatusRequest<T> {
  private String reference;
}
