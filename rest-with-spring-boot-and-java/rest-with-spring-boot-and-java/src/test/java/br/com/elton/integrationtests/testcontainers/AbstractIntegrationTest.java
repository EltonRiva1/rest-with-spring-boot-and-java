package br.com.elton.integrationtests.testcontainers;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.29");

		private static void startContainers() {
			Startables.deepStart(Stream.of(mysql)).join();
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			// TODO Auto-generated method stub
			startContainers();
			ConfigurableEnvironment configurableEnvironment = applicationContext.getEnvironment();
			MapPropertySource mapPropertySource = new MapPropertySource("mapPropertySource",
					createConnectionConfiguration());
			configurableEnvironment.getPropertySources().addFirst(mapPropertySource);
		}

		private static Map<String, Object> createConnectionConfiguration() {
			// TODO Auto-generated method stub
			return Map.of("spring.datasource.url", mysql.getJdbcUrl(), "spring.datasource.username",
					mysql.getUsername(), "spring.datasource.password", mysql.getPassword());
		}
	}

}
