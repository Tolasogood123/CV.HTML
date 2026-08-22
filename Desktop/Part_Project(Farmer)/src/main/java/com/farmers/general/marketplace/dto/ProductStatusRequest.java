package com.farmers.general.marketplace.dto;

import com.farmers.general.marketplace.enums.ProductStatus;
import lombok.Data;

@Data
public class ProductStatusRequest {

    private ProductStatus status;
}
