package se.sundsvall.educationdata.integration.db.specification;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

import static java.util.Objects.nonNull;

public class SpecificationBuilder<T> {

	private SpecificationBuilder() {}

	/**
	 * Method builds an equal filter if value is not null. If value is null, method returns an always-true predicate
	 * (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildEqualFilter(final String attribute, final Object value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.equal(entity.get(attribute), value) : cb.and();
	}

	/**
	 * Method builds a like ignore case filter if value is not null. If value is null, method returns an always-true
	 * predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildLikeIgnoreCaseFilter(final String attribute, final String value) {
		return (entity, cq, cb) -> nonNull(value)
			? cb.like(cb.lower(entity.get(attribute)), "%" + value.toLowerCase() + "%")
			: null;
	}

	/**
	 * Method builds a filter depending on sent in time stamps. If both values are null, method returns an always-true
	 * predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildDateIsEqualOrAfterFilter(final String attribute, final LocalDate value) {
		return (entity, cq, cb) -> nonNull(value) ? cb.greaterThanOrEqualTo(entity.get(attribute), value) : cb.and();
	}

	/**
	 * Method builds an equal filter for a joined table if value is not null. If value is null, method returns an
	 * always-true predicate
	 * (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildJoinedEqualFilter(
		final String joinAttribute, final String attribute, final Object value) {
		return (entity, cq, cb) -> nonNull(value)
			? cb.equal(joinOf(entity, joinAttribute).get(attribute), value)
			: cb.and();
	}

	/**
	 * Method builds an equal ignore case filter if value is not null. If value is null, method returns an always-true
	 * predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildJoinedLikeIgnoreCaseFilter(final String joinAttribute, final String attribute, final String value) {
		return (entity, cq, cb) -> nonNull(value)
			? cb.like(cb.lower(joinOf(entity, joinAttribute).get(attribute)), "%" + value.toLowerCase() + "%")
			: cb.and();
	}

	/**
	 * Method builds a filter depending on sent in time stamps. Matches dates that is equal, before or null. If both values
	 * are null, method returns an always-true predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildJoinedDateIsEqualOrBeforeOrNullFilter(final String joinAttribute, final String attribute, final LocalDateTime value) {
		return (entity, cq, cb) -> {
			if (value != null) {
				return cb.or(
					cb.lessThanOrEqualTo(joinOf(entity, joinAttribute).get(attribute), value),
					cb.isNull(joinOf(entity, joinAttribute).get(attribute)));
			}
			return cb.and();
		};
	}

	private static From<?, ?> joinOf(final From<?, ?> from, final String joinAttribute) {
		return from.getJoins().stream()
			.filter(existing -> joinAttribute.equals(existing.getAttribute().getName()))
			.map(existing -> (From<?, ?>) existing)
			.findFirst()
			.orElseGet(() -> from.join(joinAttribute, JoinType.LEFT));
	}
}
