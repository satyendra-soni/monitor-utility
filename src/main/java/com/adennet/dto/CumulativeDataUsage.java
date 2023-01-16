package com.adennet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbResponse {

	private String _id;
	private String sum_cumulativeDataUsage;
	private String sum_cumulativeDataUsage;

	private long count;

	//...
}
