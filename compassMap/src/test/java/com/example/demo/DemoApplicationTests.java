package com.example.demo;

import com.example.demo.model.Answer;
import com.example.demo.model.Customer;
import com.example.demo.model.Dimension;
import com.example.demo.model.Question;
import com.example.demo.model.QuestionAndAnswer;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionAndAnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.service.CustomerService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DemoApplicationTests {

	//@Autowired
	//private CustomerRepository customerRepository;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private QuestionAndAnswerRepository repositoryQuestionAndAnswer;
	@Autowired
	private QuestionRepository repositoryQuestion;
	@Autowired
	private AnswerRepository repositoryAnswer;

	@Test
	void contextLoads() {
	}

	@Test
	void findCustomerById (){

		Customer customerFound1 = customerService.getCustomerById("64c7be39-d974-419f-a766-a6063346676b");
		System.out.println("Customer found 1 : " + customerFound1 );

		Customer customerFound2 = customerService.getCustomerById("banana");
		System.out.println("Customer found 2 : " + customerFound2 );


	}

	@Test
	void deleteCustomerById(){

		customerService.deleteCustomer("8b4ca0f7-5de0-4ab2-a431-a849f158cd62");
	}


	@Test
	void javaFakerCreateObjects(){


		Faker faker = new Faker();

		//String name = faker.name().fullName(); // Miss Samanta Schmidt
		//String firstName = faker.name().firstName(); // Emory
		//String lastName = faker.name().lastName(); // Barton

		//String streetAddress = faker.address().streetAddress(); // 60018 Sawayn Brooks Suite 449

		//String titleBook = faker.book().title();

		Customer newCustomer = new Customer();

		for (int i= 0; i < 1000; i++){

			// id?
			newCustomer.setFirstName( faker.name().firstName()  );
			newCustomer.setLastName(  faker.name().lastName());

			customerService.createCustomer(newCustomer);
		}
	}

	@Test
	@Transactional
	void questionAndAnswerManyToOneSharesAnswerAndPersistsForeignKeys() {

		// 1. the Question catalogue row must exist first (FK question_id)
		Question question = Question.builder()
				.id(UUID.randomUUID().toString())
				.name("q-test")
				.text("Have you written code in any language before?")
				.dimension(Dimension.KNOWS_CODE)
				.order(999)
				.weight(1.0)
				.build();
		repositoryQuestion.save(question);

		// 2. the Answer catalogue row must exist first (FK answer_id)
		Answer answer = Answer.builder()
				.id(UUID.randomUUID().toString())
				.value(true)
				.build();
		repositoryAnswer.save(answer);

		// 3. two Q&A rows share the SAME Answer -> proves @ManyToOne (n..1)
		QuestionAndAnswer qa1 = QuestionAndAnswer.builder()
				.id(UUID.randomUUID().toString())
				.question(question)
				.answer(answer)
				.scoreContribution(1.0)
				.build();
		QuestionAndAnswer qa2 = QuestionAndAnswer.builder()
				.id(UUID.randomUUID().toString())
				.question(question)
				.answer(answer)
				.scoreContribution(2.0)
				.build();
		repositoryQuestionAndAnswer.save(qa1);
		repositoryQuestionAndAnswer.save(qa2);

		// 4. read back: the FK values are the ids of the referenced rows
		QuestionAndAnswer found = repositoryQuestionAndAnswer.findById(qa1.getId()).orElse(null);
		assertNotNull(found);
		assertEquals(question.getId(), found.getQuestion().getId());
		assertEquals(answer.getId(), found.getAnswer().getId());
		assertEquals(1.0, found.getScoreContribution());

		// 5. derived queries traverse the associations
		assertEquals(2, repositoryQuestionAndAnswer.findByQuestionId(question.getId()).size());
		assertEquals(2, repositoryQuestionAndAnswer.findByAnswerId(answer.getId()).size());
	}

}
