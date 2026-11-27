package com.example.clothes_website.repository.specification;

import com.example.clothes_website.dto.request.AdminProductSearchRequest;
import com.example.clothes_website.dto.request.ProductSearchRequest;
import com.example.clothes_website.entity.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProductSpecification {

    public static Specification<Product> publicFilter(ProductSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = commonPredicates(root, cb, request);
            predicates.add(cb.isTrue(root.get("isActive")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Product> adminFilter(AdminProductSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = commonPredicates(root, cb, request);
            if (request.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), request.getIsActive()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static List<Predicate> commonPredicates(Root<Product> root, CriteriaBuilder cb, ProductSearchRequest request) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isFalse(root.get("category").get("deleted")));

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            predicates.add(cb.like(
                    cb.lower(root.get("name")),
                    "%" + request.getKeyword().toLowerCase() + "%"
            ));
        }

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            predicates.add(root.get("category").get("categoryId").in(request.getCategoryIds()));
        }

        if (request.getParentCategoryId() != null) {
            predicates.add(cb.equal(
                    root.get("category").get("categoryParent").get("categoryId"),
                    request.getParentCategoryId()
            ));
        }

        if (request.getBrandIds() != null && !request.getBrandIds().isEmpty()) {
            predicates.add(root.get("brand").get("brandId").in(request.getBrandIds()));
        }

        if (request.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
        }

        if (request.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
        }

        return predicates;
    }
}
