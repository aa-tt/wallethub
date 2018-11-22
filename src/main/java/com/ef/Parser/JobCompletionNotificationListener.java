package com.ef.Parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	@Value("${startDate}")
    private String startDate;
	@Value("${duration}")
	private String duration;
	@Value("${threshold}")
	private String threshold;
	

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			String[] dates = startDate.split("\\.");
			String formattedDate = new StringBuilder(dates[0]).append(" ").append(dates[1]).toString();
			Timestamp startTime = Timestamp.valueOf(formattedDate);
			Timestamp endTime = new Timestamp(Timestamp.valueOf(formattedDate).getTime()+(duration.equals("hourly")?3600000:86400000));
			String QUERY_LOG = "SELECT ip_address, count(*) FROM LOG_TABLE"
					+ " WHERE timestamp >= '"+ startTime +"' and timestamp < '"+ endTime + "'" 
					+ " group by(ip_address)"
					+ " having count(*) > "+ Integer.parseInt(threshold) +";";
			System.out.println(QUERY_LOG);
			List<BlockedIP> results = jdbcTemplate.query(QUERY_LOG, new RowMapper<BlockedIP>() {

				@Override
				public BlockedIP mapRow(ResultSet rs, int rowNum) throws SQLException {
					BlockedIP ip = new BlockedIP();
					ip.setIp(rs.getString(1));
					ip.setHitCount(rs.getInt(2));
					return ip;
				}
			}); 
			
			for (BlockedIP result : results) {
				System.out.println("<" + result.getIp() + "> -" + result.getHitCount() + " hits.");
			}

		}
	}
	
}
