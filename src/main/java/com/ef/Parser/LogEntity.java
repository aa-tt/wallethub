package com.ef.Parser;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="log_table")
public class LogEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer logId;
	Timestamp timestamp;
	String ipAddress;
	String protocol;
	Integer status;
	String detail;

}
