package com.coamctech.bxloan.dms.dao.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Embeddable
public class CodePk implements Serializable {
	
	@Column(name = "CODE_KEY")
	private String codeKey;

	@Column(name = "CODE_TYPE")
	private String codeType;
	
	public CodePk() {

	}

	public CodePk(String codeKey, String codeType) {
		super();
		this.codeKey = codeKey;
		this.codeType = codeType;
	}

}
