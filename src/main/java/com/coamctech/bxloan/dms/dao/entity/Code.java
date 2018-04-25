package com.coamctech.bxloan.dms.dao.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.coamctech.bxloan.dms.commons.base.BaseEntity;

@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "CODE")
public class Code extends BaseEntity {

	@EmbeddedId
	private CodePk codePk;

	@Column(name = "CODE_NAME")
	private String codeName;

	@Column(name = "CODE_VALUE")
	private String codeValue;

	@Column(name = "ORDER_NUMBER")
	private Integer orderNumber;

	@Column(name = "USABLE_STATUS_IND")
	private String usableStatusInd;


}
