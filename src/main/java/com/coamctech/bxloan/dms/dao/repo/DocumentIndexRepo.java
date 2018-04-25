package com.coamctech.bxloan.dms.dao.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.coamctech.bxloan.dms.dao.entity.DocumentIndex;

public interface DocumentIndexRepo extends
		PagingAndSortingRepository<DocumentIndex, Long>,
		JpaSpecificationExecutor<DocumentIndex> {

}
