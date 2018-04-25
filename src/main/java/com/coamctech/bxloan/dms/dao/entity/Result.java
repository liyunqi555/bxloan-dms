package com.coamctech.bxloan.dms.dao.entity;

/**
 * 封装web层返回值的pojo
 *
 */
public class Result {
	private Boolean success; // 是否成功
	private String msg; // 消息
	private Object data; // 返回的数据
	public Result() {

	}

	public Result(Boolean success) {
		super();
		this.success = success;
	}

	public Result(Boolean success, Object data) {
		super();
		this.success = success;
		this.data = data;
	}

	public Result(Boolean success, String msg, Object data) {
		super();
		this.success = success;
		this.msg = msg;
		this.data = data;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}


	@Override
	public String toString() {
		return "Result [success=" + success + ", msg=" + msg + ", data=" + data
				+ "]";
	}

}
