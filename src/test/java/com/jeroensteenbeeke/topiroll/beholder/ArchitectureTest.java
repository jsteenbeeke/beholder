package com.jeroensteenbeeke.topiroll.beholder;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.annotation.NoTransactionRequired;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.AmazonS3Service;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.BeholderSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.FakeSlackHandler;
import com.jeroensteenbeeke.topiroll.beholder.beans.impl.LocalInstanceImageService;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

public class ArchitectureTest {
	@Test
	public void doNotUseImplClassesDirectly() {
		JavaClasses allClasses = new ClassFileImporter()
				.importPackages("com.jeroensteenbeeke.topiroll.beholder");

		var rule = noFields().should().haveRawType(AmazonS3Service.class)
				.orShould().haveRawType(LocalInstanceImageService.class)
				.orShould().haveRawType(BeholderSlackHandler.class).orShould()
				.haveRawType(FakeSlackHandler.class);

		rule.check(allClasses);
	}

	@Test
	public void componentOrServiceClassesShouldBeTransactional() {
		JavaClasses allClasses = new ClassFileImporter()
				.importPackages("com.jeroensteenbeeke.topiroll.beholder");

		var rule = methods()
				.that(new DescribedPredicate<>("Are not lambda methods") {
					@Override
					public boolean apply(JavaMethod input) {
						String methodName = input.getName();
						return !methodName.contains("lambda$") && !methodName
								.contains("$deserializeLambda$");
					}
				}).and().arePublic().and().areDeclaredInClassesThat()
				.areAnnotatedWith(Service.class).or().areDeclaredInClassesThat()
				.areAnnotatedWith(Repository.class).should()
				.beAnnotatedWith(Transactional.class).orShould()
				.beAnnotatedWith(NoTransactionRequired.class);

		rule.check(allClasses);
	}

	@Test
	public void ensureDAOClassesAreAnnotatedAsRepository() {
		JavaClasses allClasses = new ClassFileImporter()
				.importPackages("com.jeroensteenbeeke.topiroll.beholder");

		var rule = classes().that().areAssignableTo(HibernateDAO.class).should()
				.beAnnotatedWith(Repository.class).andShould()
				.notBeAnnotatedWith(Component.class);

		rule.check(allClasses);
	}
}
