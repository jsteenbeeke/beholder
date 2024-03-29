/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.jeroensteenbeeke.hyperion.social.beans.slack.SlackHandler;
import com.jeroensteenbeeke.hyperion.solstice.spring.TestModeEntityPopulator;
import com.jeroensteenbeeke.hyperion.solstice.spring.db.EnableSolstice;
import com.jeroensteenbeeke.topiroll.beholder.beans.IdentityService;
import com.jeroensteenbeeke.topiroll.beholder.beans.RemoteImageService;
import com.jeroensteenbeeke.topiroll.beholder.beans.data.RemoteImageData;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.LocalInstanceImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandlerBuilder.aBeholderSlackHandler;

@Configuration
@ComponentScan(
		basePackages = {"com.jeroensteenbeeke.topiroll.beholder.dao",
				"com.jeroensteenbeeke.topiroll.beholder.beans",
				"com.jeroensteenbeeke.topiroll.beholder.entities.populators"},
		scopedProxy = ScopedProxyMode.TARGET_CLASS)
@EnableTransactionManagement
@EnableSolstice(entityBasePackage = "com.jeroensteenbeeke.topiroll.beholder.entities", liquibaseChangelog = "classpath:/com/jeroensteenbeeke/topiroll/beholder/entities/liquibase/db.changelog-master.xml")
@PropertySource("file:${hyperion.configdir:${user.home}/.hyperion}/beholder-web.properties")
public class BeholderApplicationConfig {

	@Bean
	public TestModeEntityPopulator testPopulator() {
		return new TestModeEntityPopulator();
	}

	@Bean
	@Conditional(AmazonEnabledCondition.class)
	public TransferManager transferManager(@Value("${amazon.clientid}") String clientId,
										   @Value("${amazon.clientsecret}") String clientSecret,
										   @Value("${amazon.region}") String region

	) {
		AWSCredentials credentials = new BasicAWSCredentials(clientId, clientSecret);
		AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);

		return TransferManagerBuilder.standard().withS3Client(AmazonS3ClientBuilder
																	  .standard()
																	  .withCredentials(provider)
																	  .withRegion(region)
																	  .build()).build();

	}

	@Bean
	@Conditional(AmazonEnabledCondition.class)
	public RemoteImageService amazonImageService(TransferManager manager, AmazonS3 amazonS3,
												 @Value("${amazon.bucketname}") String amazonBucketName) {
		return new AmazonS3Service(manager, amazonS3, amazonBucketName);
	}

	@Bean
	@Conditional(NoAmazonCondition.class)
	public RemoteImageService localImageService() {
		return new LocalInstanceImageService();
	}

	@Bean
	@Conditional(AmazonEnabledCondition.class)
	public AmazonS3 amazonS3(@Value("${amazon.clientid}") String clientId,
							 @Value("${amazon.clientsecret}") String clientSecret,
							 @Value("${amazon.region}") String region

	) {
		AWSCredentials credentials = new BasicAWSCredentials(clientId, clientSecret);
		AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);

		return AmazonS3ClientBuilder
				.standard()
				.withRegion(region)
				.withCredentials(provider)
				.build();
	}

	@Bean
	public RemoteImageData remoteImageData(@Value("${remote.image.url.prefix}") String urlPrefix) {
		return new RemoteImageData(urlPrefix);
	}

	@Bean
	@Conditional(SlackEnabledCondition.class)
	public SlackHandler regularSlackHandler(@Value("${application.baseurl}") String applicationBaseUrl,
											@Value("${slack.clientid}") String clientId,
											@Value("${slack.clientsecret}") String clientSecret,
											@Value("${slack.signingsecret}") String signingSecret,
											@Value("${slack.deploy.webhook:}") String deployHook,
											@Value("${rollbar.environment:localhost}") String environmentName,
											@Value("${rollbar.localuser:${user.name}}") String deployingInstance,
											IdentityService identityService) {
		return aBeholderSlackHandler().withApplicationBaseUrl(applicationBaseUrl)
									  .withClientId(clientId)
									  .withClientSecret(clientSecret)
									  .withSigningSecret(signingSecret)
									  .withEnvironmentName(environmentName)
									  .withDeployingInstance(deployingInstance)
									  .withIdentityService(identityService)
									  .withDeployWebhook(deployHook)
									  .build();
	}

	@Bean
	@Conditional(NoSlackCondition.class)
	public SlackHandler fakeSlackHandler(@Value("${application.baseurl}") String applicationBaseUrl, IdentityService identityService) {
		return new FakeSlackHandler(applicationBaseUrl, identityService);
	}
}
