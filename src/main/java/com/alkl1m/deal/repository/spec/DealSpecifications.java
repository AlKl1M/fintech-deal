package com.alkl1m.deal.repository.spec;

import com.alkl1m.deal.domain.entity.Contractor;
import com.alkl1m.deal.domain.entity.Deal;
import com.alkl1m.deal.domain.entity.Role;
import com.alkl1m.deal.domain.exception.DateFormatException;
import com.alkl1m.deal.web.payload.ContractorFilterDto;
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
import java.util.Optional;
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
            addInPredicate(predicates, root, criteriaBuilder, "type", payload.type());
            addInPredicate(predicates, root, criteriaBuilder, "status", payload.status());
            addContractorPredicate(predicates, root, criteriaBuilder, payload.borrower(), "BORROWER");
            addContractorPredicate(predicates, root, criteriaBuilder, payload.warranty(), "WARRANTY");

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

    private static void addInPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String attributeName, List<?> attributeValues) {
        if (attributeValues != null && !attributeValues.isEmpty()) {
            predicates.add(root.get(attributeName).in(attributeValues));
        }
    }

    private static void addDateRangePredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, String dateRange) {
        Optional.ofNullable(dateRange)
                .filter(range -> !range.isEmpty())
                .map(range -> range.split(" - "))
                .filter(dates -> dates.length == 2)
                .ifPresent(dates -> {
                    try {
                        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(dates[0]);
                        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(dates[1]);
                        predicates.add(criteriaBuilder.between(root.get(field), startDate, endDate));
                    } catch (ParseException e) {
                        throw new DateFormatException("Date format is yyyy-MM-dd - yyyy-MM-dd");
                    }
                });
    }

    private static void addContractorPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, ContractorFilterDto contractor, String category) {
        if (contractor != null) {
            Join<Deal, Contractor> contractorJoin = root.join("contractors");
            Join<Contractor, Role> roleJoin = contractorJoin.join("roles");
            Predicate contractorPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(contractorJoin.get("contractorId"), contractor.contractorId()),
                    criteriaBuilder.like(contractorJoin.get("name"), contractor.name()),
                    criteriaBuilder.like(contractorJoin.get("inn"), contractor.inn())
            );
            predicates.add(criteriaBuilder.equal(roleJoin.get("category"), category));
            predicates.add(contractorPredicate);
        }
    }

}
