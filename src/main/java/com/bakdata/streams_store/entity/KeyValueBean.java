package com.bakdata.streams_store.entity;

import lombok.*;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValueBean {
  private String key;
  private String value;
}
