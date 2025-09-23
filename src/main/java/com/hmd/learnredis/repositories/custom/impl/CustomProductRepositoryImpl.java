package com.hmd.learnredis.repositories.custom.impl;

import com.hmd.learnredis.dtos.requests.SearchProductRequest;
import com.hmd.learnredis.models.Product;
import com.hmd.learnredis.repositories.custom.CustomProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomProductRepositoryImpl implements CustomProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private List<Predicate> buildPredicates(SearchProductRequest request, CriteriaBuilder cb, Root<Product> root) {
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.hasText(request.getName()))
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));
        if (request.getPriceFrom() != null)
            predicates.add(cb.ge(root.get("price"), request.getPriceFrom()));
        if (request.getPriceTo() != null)
            predicates.add(cb.le(root.get("price"), request.getPriceTo()));
        return predicates;
    }

    @Override
    public Page<Product> searchProducts(SearchProductRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);
        List<Predicate> predicates = buildPredicates(request, cb, root);
        if (!predicates.isEmpty())
            cq.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Order> orders = new ArrayList<>();
        if (request.getSortByPrice())
            orders.add(request.getPriceDesc() ? cb.desc(root.get("price")) : cb.asc(root.get("price")));
        cq.orderBy(orders);

        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize());
        TypedQuery<Product> tq = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        CriteriaQuery<Long> countQ = cb.createQuery(Long.class);
        Root<Product> countRoot = countQ.from(Product.class);
        List<Predicate> countPredicates = buildPredicates(request, cb, countRoot);
        if (!countPredicates.isEmpty())
            countQ.select(cb.countDistinct(countRoot.get("id"))).where(cb.and(countPredicates.toArray(new Predicate[0])));
        else
            countQ.select(cb.count(countRoot));
        long total = entityManager.createQuery(countQ).getSingleResult();

        return new PageImpl<>(tq.getResultList(), pageable, total);
    }
}
