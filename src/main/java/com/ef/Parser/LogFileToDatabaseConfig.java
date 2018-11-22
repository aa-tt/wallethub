package com.ef.Parser;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@EnableBatchProcessing
@Configuration
public class LogFileToDatabaseConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	JobCompletionNotificationListener listener;

	@Autowired
	DataSource dataSource;
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Value("${accesslog}")
	private String accesslog;

	private static final String QUERY_INSERT_LOG = "INSERT " +
			"INTO log_table(log_id, timestamp, ip_address, protocol, status, detail) " +
			"VALUES (nextval('hibernate_sequence'), ?, ?, ?, ?, ?)";

	@Bean
	@StepScope
	public FlatFileItemReader<LogDto> reader() {
		FlatFileItemReader<LogDto> reader = new FlatFileItemReader<LogDto>();
		// reader.setResource(new ClassPathResource("access.log"));
		if (accesslog.contains(".log"))
			reader.setResource(new FileSystemResource(new File(accesslog)));
		else
			reader.setResource(new FileSystemResource(new File(accesslog + File.separatorChar + "access.log")));
		reader.setLineMapper(new DefaultLineMapper<LogDto>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setDelimiter("|");
						setNames(new String[] { "timestamp", "ip_address", "protocol", "status", "detail" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<LogDto>() {
					{
						setTargetType(LogDto.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	ItemProcessor<LogDto, LogEntity> processor() {
		return new ItemProcessor<LogDto, LogEntity>() {
			@Override
			public LogEntity process(LogDto item) throws Exception {
				Timestamp date = java.sql.Timestamp.valueOf(item.getTimestamp());
				return new LogEntity() {
					{
						setTimestamp(date);
						setIpAddress(item.getIpAddress());
						setProtocol(item.getProtocol());
						setStatus(item.getStatus());
						setDetail(item.getDetail());
					}
				};
			}
		};
	}

	@Bean
	public ItemWriter<LogEntity> writer(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {

		JdbcBatchItemWriter<LogEntity> itemWriter = new JdbcBatchItemWriter<>();
		itemWriter.setDataSource(dataSource);
		itemWriter.setJdbcTemplate(jdbcTemplate);

		itemWriter.setSql(QUERY_INSERT_LOG);

		ItemPreparedStatementSetter<LogEntity> valueSetter = new LogPreparedStatementSetter();
		itemWriter.setItemPreparedStatementSetter(valueSetter);

		return itemWriter;
	}

	@Bean("logToDatabaseStep")
	public Step logToDatabaseStep() {
		return stepBuilderFactory.get("logToDatabaseStep").<LogDto, LogEntity>chunk(1000).reader(reader())
				.processor(processor()).writer(writer(dataSource, jdbcTemplate)).build();
	}

	@Bean("logToDatabaseJob")
	Job logToDatabaseJob() {
		return jobBuilderFactory.get("logToDatabaseJob").incrementer(new RunIdIncrementer()).listener(listener)
				.flow(logToDatabaseStep()).end().build();
	}

	final class LogPreparedStatementSetter implements ItemPreparedStatementSetter<LogEntity> {

		@Override
		public void setValues(LogEntity log, PreparedStatement preparedStatement) throws SQLException {
			preparedStatement.setTimestamp(1, log.getTimestamp());
			preparedStatement.setString(2, log.getIpAddress());
			preparedStatement.setString(3, log.getProtocol());
			preparedStatement.setInt(4, log.getStatus());
			preparedStatement.setString(5, log.getDetail());
		}
	}

}

