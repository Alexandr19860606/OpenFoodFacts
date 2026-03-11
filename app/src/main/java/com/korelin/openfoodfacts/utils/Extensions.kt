package com.korelin.openfoodfacts.utils

import com.korelin.openfoodfacts.data.model.ProductInfo


fun safeGetBarcode(product: ProductInfo?): String? {
    return product?.code
}

