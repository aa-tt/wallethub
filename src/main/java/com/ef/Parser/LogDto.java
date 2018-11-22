package com.ef.Parser;

import lombok.Data;

@Data
public class LogDto {
	
	Integer logId;
	String timestamp;
	String ipAddress;
	String protocol;
	Integer status;
	String detail;

}
