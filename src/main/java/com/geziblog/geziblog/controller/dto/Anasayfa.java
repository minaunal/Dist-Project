package com.geziblog.geziblog.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Anasayfa {
    String username;
    String baslik;
    String metin;

}
