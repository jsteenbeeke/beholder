/**
 * This file is part of Beholder
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder;

import javax.persistence.EntityManagerFactory;

import org.jboss.resteasy.plugins.spring.SpringBeanProcessorServletAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jeroensteenbeeke.hyperion.solstice.spring.TestModeEntityPopulator;
import com.jeroensteenbeeke.hyperion.solstice.spring.db.SolsticeConfig;
import com.zaxxer.hikari.HikariDataSource;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ComponentScan(
		basePackages = { "com.jeroensteenbeeke.topiroll.beholder.dao.hibernate",
				"com.jeroensteenbeeke.topiroll.beholder.beans.impl",
				"com.jeroensteenbeeke.topiroll.beholder.entities.populators" },
		scopedProxy = ScopedProxyMode.INTERFACES)
@EnableTransactionManagement
@Import(SolsticeConfig.class)
@PropertySource("file:${hyperion.configdir:${user.home}/.hyperion}/beholder-web.properties")
public class BeholderApplicationConfig {
	@Bean
	public SpringLiquibase liquibase(
			@Value("${liquibase.contexts:production}") String liquibaseContexts,
			HikariDataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(
				"classpath:/com/jeroensteenbeeke/topiroll/beholder/entities/liquibase/db.changelog-master.xml");
		liquibase.setContexts(liquibaseContexts);

		return liquibase;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			HikariDataSource dataSource, JpaVendorAdapter vendorAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setPackagesToScan(
				"com.jeroensteenbeeke.topiroll.beholder.entities");
		factory.setJpaVendorAdapter(vendorAdapter);

		return factory;
	}

	@Bean
	public JpaTransactionManager transactionManager(
			EntityManagerFactory factory) {
		JpaTransactionManager manager = new JpaTransactionManager();
		manager.setEntityManagerFactory(factory);

		return manager;
	}

	@Bean
	public TestModeEntityPopulator testPopulator() {
		return new TestModeEntityPopulator();
	}

	@Bean
	public static SpringBeanProcessorServletAware resteasyConfig() {
		return new SpringBeanProcessorServletAware();
	}
}