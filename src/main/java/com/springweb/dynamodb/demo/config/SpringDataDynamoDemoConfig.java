package com.springweb.dynamodb.demo.config;

import org.apache.commons.lang3.StringUtils;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBOperations;
import org.socialsignin.spring.data.dynamodb.core.DynamoDBTemplate;
import org.socialsignin.spring.data.dynamodb.mapping.event.ValidatingDynamoDBEventListener;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Our main configuration class.
 * 
 * To plug-in AmazonDB specific spring-data crud repositories into your
 * application, @EnableDynamoDBRepositories for the packages containing your
 * repository interfaces. NB. The AmazonDB repositories support only simple CRUD
 * operations at this time as no query lookup strategy framework has yet been
 * implemented which would be needed to support custom finder methods.
 * 
 * To export these repositories as REST resources, import
 * RepositoryRestMvcConfiguration (or your own custom version to exert control
 * of url building etc)
 * 
 * 
 * @author Pavan KS
 * 
 */
@Configuration
@EnableWebMvc
@EnableDynamoDBRepositories(basePackages = "com.springweb.dynamodb.demo.repository", dynamoDBOperationsRef = "dynamoDBOperations")
@Import(value = DemoRestMvcConfiguration.class)
public class SpringDataDynamoDemoConfig {

	@Value("${amazon.dynamodb.endpoint}")
	private String amazonDynamoDBEndpoint;

	@Value("${amazon.aws.accesskey}")
	private String amazonAWSAccessKey;

	@Value("${amazon.aws.secretkey}")
	private String amazonAWSSecretKey;

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(
				amazonAWSCredentials());
		if (StringUtils.isNotEmpty(amazonDynamoDBEndpoint)) {
			amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
		}
		return amazonDynamoDB;
	}

	@Bean
	public DynamoDBOperations dynamoDBOperations() {
		return new DynamoDBTemplate(amazonDynamoDB());
	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
	}

	/**
	 * The following validation-related beans are optional - only required if
	 * JSR 303 validation is desired. For validation to work, the @EnableDynamoDBRepositories
	 * must be configured with a reference to DynamoDBOperations bean, rather
	 * than with reference to AmazonDynamoDB client
	 * */

	@Bean
	public LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public ValidatingDynamoDBEventListener validatingDynamoDBEventListener() {
		return new ValidatingDynamoDBEventListener(validator());
	}

}
