package com.ef.Parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ParserApplication /*implements ApplicationRunner*/ {

	public static void main(String[] args) {
		SpringApplication.run(ParserApplication.class, args);
	}

	/*@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.printf("App starts with- %s\n", Arrays.toString(args.getSourceArgs()));
		System.out.printf("Nonoptionargs- %s\n", args.getNonOptionArgs());
		System.out.printf("optionnames- %s\n", args.getOptionNames());
		if (args.containsOption("accesslog")) {
		}
		for (String name : args.getOptionNames()) {
			System.out.println(name + "-" + args.getOptionValues(name));
		}
	}*/
}
