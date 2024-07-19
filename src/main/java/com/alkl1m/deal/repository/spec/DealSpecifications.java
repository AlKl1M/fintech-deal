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

/**
 * Спецификация для фильтрации сделок.
 *
 * @author alkl1m
 */
public final class DealSpecifications {

    private DealSpecifications() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Метод для фильтрации сделок.
     *
     * @param payload список фильтров.
     * @return спецификация сделок.
     */
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

    /**
     * Метод добавляет предикат типа LIKE в список предикатов.
     *
     * @param predicates список предикатов
     * @param root корень для запроса Criteria API
     * @param criteriaBuilder построитель критериев
     * @param field поле для сравнения
     * @param value значение для сравнения
     */
    private static void addLikePredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, String value) {
        if (value != null) {
            predicates.add(criteriaBuilder.like(root.get(field), value));
        }
    }

    /**
     * Метод добавляет предикат типа EQUAL в список предикатов.
     *
     * @param predicates список предикатов
     * @param root корень для запроса Criteria API
     * @param criteriaBuilder построитель критериев
     * @param field поле для сравнения
     * @param value значение для сравнения
     */
    private static void addEqualPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, String value) {
        if (value != null) {
            predicates.add(criteriaBuilder.equal(root.get(field), value));
        }
    }

    /**
     * Метод добавляет предикат типа EQUAL в список предикатов для UUID значения.
     *
     * @param predicates список предикатов
     * @param root корень для запроса Criteria API
     * @param criteriaBuilder построитель критериев
     * @param field поле для сравнения
     * @param value UUID значение для сравнения
     */
    private static void addEqualPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String field, UUID value) {
        if (value != null) {
            predicates.add(criteriaBuilder.equal(root.get(field), value));
        }
    }

    /**
     * Метод добавляет предикат типа IN в список предикатов.
     *
     * @param predicates список предикатов
     * @param root корень для запроса Criteria API
     * @param criteriaBuilder построитель критериев
     * @param attributeName имя атрибута
     * @param attributeValues список значений для проверки на принадлежность
     */
    private static void addInPredicate(List<Predicate> predicates, Root<Deal> root, CriteriaBuilder criteriaBuilder, String attributeName, List<?> attributeValues) {
        if (attributeValues != null && !attributeValues.isEmpty()) {
            predicates.add(root.get(attributeName).in(attributeValues));
        }
    }

    /**
     * Метод добавляет предикат для диапазона дат в список предикатов.
     *
     * @param predicates список предикатов
     * @param root корень для запроса Criteria API
     * @param criteriaBuilder построитель критериев
     * @param field поле с датой для сравнения
     * @param dateRange строка с диапазоном дат в формате "yyyy-MM-dd - yyyy-MM-dd"
     */
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

    /**
     * Метод добавляет предикат для контрактного исполнителя в список предикатов.
     *
     * @param predicates список предикатов
     * @param root корень для запроса Criteria API
     * @param criteriaBuilder построитель критериев
     * @param contractor данные о контрактном исполнителе для фильтрации
     * @param category категория контрактного исполнителя
     */
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
