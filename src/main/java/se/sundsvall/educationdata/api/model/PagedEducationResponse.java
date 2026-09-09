package se.sundsvall.educationdata.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.sundsvall.dept44.models.api.paging.PagingAndSortingMetaData;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class PagedEducationResponse {

	@ArraySchema(schema = @Schema(implementation = Education.class, accessMode = Schema.AccessMode.READ_ONLY))
	private List<Education> educations;

	@JsonProperty("_meta")
	@Schema(implementation = PagingAndSortingMetaData.class, accessMode = Schema.AccessMode.READ_ONLY)
	private PagingAndSortingMetaData metaData;
}
