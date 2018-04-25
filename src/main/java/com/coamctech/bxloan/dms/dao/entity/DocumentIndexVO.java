package com.coamctech.bxloan.dms.dao.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class DocumentIndexVO{
	
	private Long documentId;
	private Long partyId;
	private String customerNum;
	private String userNum;
	private String documentNum;
	private String documentName;
	private String documentType;
	private Long bizId;
	private String bizNum;
	private String createUserNum;
	private String createOrgCd;
	private String createTypeCd;
	private String documentRoute;
	private String status;
	private String fileType;
	private String custDocType;
	private String patchTypeCd;
	private String allDocType;
	
	private String objId;
	/**
	 * 文档审批表示：0 普通，1退回补件，2附条件同意
	 */
	private String documentApproveType;
	
	private Long subcontractId;
	
	
}
