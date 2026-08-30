package com.venus.crud.mapper.jpa.product;

import com.venus.crud.config.VenusMapperConfig;
import com.venus.crud.dto.jpa.patch.product.ProductPatchRequest;
import com.venus.crud.dto.jpa.request.product.ProductRequest;
import com.venus.crud.dto.jpa.response.product.ProductResponse;
import com.venus.crud.entity.product.Brand;
import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.product.ProductCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = VenusMapperConfig.class)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "brand", source = "brandId")
    @Mapping(target = "productCategory", source = "productCategoryId")
    Product toEntity(ProductRequest request);

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "productCategoryId", source = "productCategory.id")
    ProductResponse toResponse(Product entity);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(ProductRequest request, @MappingTarget Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "brand", source = "brandId")
    @Mapping(target = "productCategory", source = "productCategoryId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchEntity(ProductPatchRequest request, @MappingTarget Product entity);

    default Brand mapBrand(Long brandId) {
        if (brandId == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setId(brandId);
        return brand;
    }

    default ProductCategory mapProductCategory(Long productCategoryId) {
        if (productCategoryId == null) {
            return null;
        }
        ProductCategory productCategory = new ProductCategory();
        productCategory.setId(productCategoryId);
        return productCategory;
    }
}
