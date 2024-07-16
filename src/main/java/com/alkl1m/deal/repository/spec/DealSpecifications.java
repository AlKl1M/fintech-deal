package com.alkl1m.deal.repository.spec;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Role;
import com.alkl1m.deal.web.payload.DealFiltersPayload;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class DealSpecifications {

    private DealSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    public static Specification<Deal> getDealByParameters(DealFiltersPayload payload) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isTrue(root.get("isActive")));

            addEqualPredicate(predicates, root, criteriaBuilder, "id", payload.id());
            addEqualPredicate(predicates, root, criteriaBuilder, "description", payload.description());
            addLikePredicate(predicates, root, criteriaBuilder, "agreementNumber", payload.agreementNumber());
            addDateRangePredicate(predicates, root, criteriaBuilder, "agreementDate", payload.agreementDateRange());
            addDateRangePredicate(predicates, root, criteriaBuilder, "availabilityDate", payload.availabilityDateRange());
            addDateRangePredicate(predicates, root, criteriaBuilder, "closeDt", payload.closeDtRange());

            if (payload.type() != null && !payload.type().isEmpty()) {
                predicates.add(root.get("type").in(payload.type()));
            }

            if (payload.status() != null && !payload.status().isEmpty()) {
                predicates.add(root.get("status").in(payload.status()));
            }

            if (payload.borrower() != null) {
                Join<Deal, Contractor> contractorJoin = root.join("contractors");
                Join<Contractor, Role> roleJoin = contractorJoin.join("roles");
                Predicate warrantyPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(contractorJoin.get("contractorId"), payload.borrower().contractorId()),
                        criteriaBuilder.like(contractorJoin.get("name"), payload.borrower().name()),
                        criteriaBuilder.like(contractorJoin.get("inn"), payload.borrower().inn())
                );
                predicates.add(criteriaBuilder.equal(roleJoin.get("category"), "BORROWER"));
                predicates.add(warrantyPredicate);
            }

            if (payload.warranty() != null) {
                Join<Deal, Contractor> contractorJoin = root.join("contractors");
                Join<Contractor, Role> roleJoin = contractorJoin.join("roles");
                Predicate warrantyPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(contractorJoin.get("contractorId"), payload.warranty().contractorId()),
                        criteriaBuilder.like(contractorJoin.get("name"), payload.warranty().name()),
                        criteriaBuilder.like(contractorJoin.get("inn"), payload.warranty().inn())
                );
                predicates.add(criteriaBuilder.equal(roleJoin.get("category"), "WARRANTY"));
                predicates.add(warrantyPredicate);
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addLikePredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, String value) {
        if (value != null) {
            predicates.add(criteriaBuilder.like(root.get(field), value));
        }
    }

    private static void addEqualPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, String value) {
        if (value != null) {
            predicates.add(criteriaBuilder.equal(root.get(field), value));
        }
    }

    private static void addEqualPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, UUID value) {
        if (value != null) {
            predicates.add(criteriaBuilder.equal(root.get(field), value));
        }
    }

    private static void addDateRangePredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, String dateRange) {
        if (dateRange != null && !dateRange.isEmpty()) {
            String[] dates = dateRange.split(" - ");
            if (dates.length == 2) {
                try {
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(dates[0]);
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(dates[1]);
                    predicates.add(criteriaBuilder.between(root.get(field), startDate, endDate));
                } catch (ParseException e) {

                }
            }
        }
    }
}
