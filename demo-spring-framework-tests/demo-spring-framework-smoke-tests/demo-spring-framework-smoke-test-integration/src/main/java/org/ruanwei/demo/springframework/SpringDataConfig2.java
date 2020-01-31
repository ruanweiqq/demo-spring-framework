package org.ruanwei.demo.springframework;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.RowMapperMap;
import org.springframework.data.jdbc.repository.config.ConfigurableRowMapperMap;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.jdbc.core.ColumnMapRowMapper;

@Import(JdbcConfiguration.class)
@EnableJdbcRepositories
@Configuration
public class SpringDataConfig2 {
	private static Log log = LogFactory.getLog(SpringDataConfig2.class);


	// for spring data jdbc
	@Bean
	public RowMapperMap rowMappers() {
		return new ConfigurableRowMapperMap().register(Map.class, new ColumnMapRowMapper());
	}

}
