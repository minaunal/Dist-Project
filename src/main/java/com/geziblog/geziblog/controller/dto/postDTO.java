package com.geziblog.geziblog.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class postDTO {
    String baslik;
    String metin;
    String placeNames;
}

