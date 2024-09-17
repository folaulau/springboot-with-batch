package com.folautech.batch;

import com.folautech.batch.entity.User;
import com.folautech.batch.entity.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class SpringbootWithBatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWithBatchApplication.class, args);
	}

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("loadTickers")
	private Job loadTickers;

	@Autowired
	private UserRepository userRepository;

	@Order(Integer.MAX_VALUE)
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			// Display Environmental Useful Variables
			try {
				System.out.println("\n");
				Runtime runtime = Runtime.getRuntime();
				double mb = 1048576;// megabtye to byte
				double gb = 1073741824;// gigabyte to byte
				Environment env = ctx.getEnvironment();
				TimeZone timeZone = TimeZone.getDefault();

				String hasuraServerPort = env.getProperty("server.hasura.port");

				System.out.println("************************ Springboot with Batch ***********************************");
				System.out.println("** Active Profile: " + Arrays.toString(env.getActiveProfiles()));
				System.out.println("** Port: " + env.getProperty("server.port"));
				System.out.println("** Timezone: " + timeZone.getID());
				System.out.println("** TimeStamp: " + new Date().toInstant().toString());

				System.out.println("** Internal Url: http://localhost:"
						+ env.getProperty("server.port"));

				System.out.println("** Internal Swagger: http://localhost:"
						+ env.getProperty("server.port") +  "/swagger-ui/index.html");



				System.out.println("************************* Java - JVM *********************************");
				System.out.println("** Number of processors: " + runtime.availableProcessors());
				String processName = ManagementFactory.getRuntimeMXBean().getName();
				System.out.println("** Process ID: " + processName.split("@")[0]);
				System.out.println("** Total memory: " + (runtime.totalMemory() / mb) + " MB = "
						+ (runtime.totalMemory() / gb) + " GB");
				System.out.println(
						"** Max memory: " + (runtime.maxMemory() / mb) + " MB = " + (runtime.maxMemory() / gb) + " GB");
				System.out.println("** Free memory: " + (runtime.freeMemory() / mb) + " MB = "
						+ (runtime.freeMemory() / gb) + " GB");
				System.out.println();
				System.out.println("**********************************************************************");

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Exception, commandlineRunner -> " + e.getMessage());
			}
			System.out.println("\n");
		};
	}

	@Override
	public void run(String... args) throws Exception {

//		JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).addString("accessToken", "test-accessToken").toJobParameters();
//
//		try {
//			jobLauncher.run(loadTickers, jobParameters);
//		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
//				 JobParametersInvalidException e) {
//			log.warn("Exception ,msg={}",e.getMessage());
//			e.printStackTrace();
//		}

		 Faker faker = new Faker();

		for (int i = 1; i <= 27; i++) {
			String firstName = faker.name().firstName();
			String lastName = faker.name().lastName();
			String email = (firstName+lastName).toLowerCase()+"@gmail.com";
			User user = User.builder()
					.id((long)i)
					.firstName(firstName)
					.lastName(lastName)
					.email(email)
					.build();
			userRepository.saveAndFlush(user);
		}

	}
}
