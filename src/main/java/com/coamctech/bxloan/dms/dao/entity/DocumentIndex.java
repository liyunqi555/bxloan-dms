package com.coamctech.bxloan.dms.dao.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.coamctech.bxloan.dms.commons.base.BaseEntity;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "DOCUMENT_INDEX")
public class DocumentIndex extends BaseEntity{
	@SequenceGenerator(name = "generator", sequenceName="SEQ_DOCUMENT_INDEX", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
	@Column(name = "DOCUMENT_ID", unique = true, nullable = false, precision = 12, scale = 0)
	private Long documentId;
	private Long partyId;
	private String customerNum;
	private String userNum;
	private String documentNum;
	private String documentName;
	private String documentType;
	private Long bizId;
	private String bizNum;
	private Date createDateTime;
	private Date systemUpdateTime;
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
}
