package se.sundsvall.educationdata.integration.db.specification;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity;
import se.sundsvall.educationdata.integration.db.model.EducationEventEntity_;
import se.sundsvall.educationdata.integration.db.model.EducationInfoEntity_;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity;
import se.sundsvall.educationdata.integration.db.model.EventCategoryEntity_;
import se.sundsvall.educationdata.integration.db.model.GyProgramCategoryEntity;
import se.sundsvall.educationdata.integration.db.model.GyProgramCategoryEntity_;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity;
import se.sundsvall.educationdata.integration.db.model.ReferenceCategoryEntity_;

import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.END_DATE;
import static se.sundsvall.educationdata.integration.db.model.EducationEventEntity_.START_DATE;

public class SpecificationBuilder {

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
		return (entity, cq, cb) -> {
			if (nonNull(value)) {
				return cb.equal(entity.get(attribute), value);
			}
			return cb.and();
		};
	}

	/**
	 * Method builds an equal filter if value is not null. If value is null, method returns an always-true predicate
	 * (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  values    values (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildEqualFilterIn(final String attribute, final List<String> values) {
		return (entity, cq, cb) -> {
			if (isEmpty(values)) {
				return cb.and();
			}
			return entity.get(attribute).in(values);

		};
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
		return (entity, cq, cb) -> {
			if (nonNull(value)) {
				return cb.like(cb.lower(entity.get(attribute)), "%" + value.toLowerCase() + "%");
			}
			return cb.and();
		};
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
		return (entity, cq, cb) -> {
			if (nonNull(value)) {
				return cb.greaterThanOrEqualTo(entity.get(attribute), value);
			}
			return cb.and();
		};
	}

	public static <T> Specification<T> buildWithinPeriodFilter(final LocalDate from, final LocalDate to) {
		return (entity, cq, cb) -> cb.or(
			cb.isNull(entity.get(START_DATE)),
			cb.isNull(entity.get(END_DATE)),
			cb.and(
				cb.lessThanOrEqualTo(entity.get(START_DATE), to),
				cb.greaterThanOrEqualTo(entity.get(END_DATE), from)));
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
		return (entity, cq, cb) -> {
			if (nonNull(value)) {
				return cb.equal(joinOf(entity, joinAttribute).get(attribute), value);
			}
			return cb.and();
		};
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
		return (entity, cq, cb) -> {
			if (nonNull(value)) {
				return cb.like(cb.lower(joinOf(entity, joinAttribute).get(attribute)), "%" + value.toLowerCase() + "%");
			}
			return cb.and();
		};
	}

	/**
	 * Method builds a filter depending on sent in time stamps. Matches dates that is equal or before. If value
	 * is null, method returns an always-true predicate (meaning no filtering will be applied for sent in attribute)
	 *
	 * @param  attribute name that will be used in filter
	 * @param  value     value (or null) to compare against
	 * @return           Specification<T> matching sent in comparison
	 */
	public static <T> Specification<T> buildJoinedDateTimeIsEqualOrBeforeFilter(final String joinAttribute, final String attribute, final LocalDateTime value) {
		return (entity, cq, cb) -> {
			if (value != null) {
				return cb.lessThanOrEqualTo(joinOf(entity, joinAttribute).get(attribute), value);
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

	public static <T> Specification<T> buildIgnoreCaseFilterWithList(final String attribute, List<String> values) {

		return (entity, cq, cb) -> {
			if (values == null || values.isEmpty()) {
				return cb.and();
			}
			return cb.lower(entity.get(attribute)).in(values.stream()
				.map(value -> value.strip().toLowerCase())
				.distinct()
				.toList());
		};
	}

	public static Specification<EducationEventEntity> buildCategoryFilter(final List<String> categories) {
		return (entity, cq, cb) -> {
			if (categories == null || categories.isEmpty()) {
				return cb.and();
			}

			final var sub = cq.subquery(String.class);
			final var eventCategory = sub.from(EventCategoryEntity.class);
			final var referenceCategory = sub.from(ReferenceCategoryEntity.class);

			final var normalizedCategories = categories.stream()
				.map(value -> value.strip().toLowerCase())
				.distinct()
				.toList();

			sub.select(eventCategory.get(EventCategoryEntity_.EDUCATION_EVENT_ID))
				.where(
					cb.equal(
						eventCategory.get(EventCategoryEntity_.DIRECTION_ID),
						referenceCategory.get(ReferenceCategoryEntity_.DIRECTION_ID)),
					cb.lower(
						referenceCategory.get(ReferenceCategoryEntity_.CATEGORY_NAME)).in(normalizedCategories));
			return entity.get(EducationEventEntity_.EDUCATION_EVENT_ID).in(sub);
		};
	}

	public static Specification<EducationEventEntity> buildDirectionFilter(final List<String> direction) {
		return (entity, cq, cb) -> {
			if (direction == null || direction.isEmpty()) {
				return cb.and();
			}

			final var sub = cq.subquery(String.class);
			final var eventCategory = sub.from(EventCategoryEntity.class);
			final var referenceCategory = sub.from(ReferenceCategoryEntity.class);

			final var normalizedDirections = direction.stream()
				.map(value -> value.strip().toLowerCase())
				.distinct()
				.toList();

			sub.select(eventCategory.get(EventCategoryEntity_.EDUCATION_EVENT_ID))
				.where(
					cb.equal(
						eventCategory.get(EventCategoryEntity_.DIRECTION_ID),
						referenceCategory.get(ReferenceCategoryEntity_.DIRECTION_ID)),
					cb.lower(
						referenceCategory.get(ReferenceCategoryEntity_.DIRECTION_NAME)).in(normalizedDirections));
			return entity.get(EducationEventEntity_.EDUCATION_EVENT_ID).in(sub);
		};
	}

	public static Specification<EducationEventEntity> buildGyCategoryFilter(
		final List<String> categories) {

		return (entity, cq, cb) -> {
			if (categories == null || categories.isEmpty()) {
				return cb.and();
			}

			final var info = joinOf(
				entity, EducationEventEntity_.EDUCATION_INFO);

			final var sub = cq.subquery(Integer.class);
			final var gyProgramCategory = sub.from(GyProgramCategoryEntity.class);

			final var normalizedCategories = categories.stream()
				.map(value -> value.strip().toLowerCase())
				.distinct()
				.toList();

			sub.select(cb.literal(1))
				.where(
					cb.equal(
						info.get(EducationInfoEntity_.SCHOOL_TYPE), "GY"),
					cb.equal(
						cb.substring(info.get(EducationInfoEntity_.CODE), 1, 2),
						gyProgramCategory.get(GyProgramCategoryEntity_.PROGRAM_CODE)),
					cb.lower(
						gyProgramCategory.get(GyProgramCategoryEntity_.CATEGORY)).in(normalizedCategories));

			return cb.exists(sub);
		};
	}
}
