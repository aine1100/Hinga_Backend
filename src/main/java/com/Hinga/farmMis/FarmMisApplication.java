package com.Hinga.farmMis;

import com.Hinga.farmMis.Model.Address;
import com.Hinga.farmMis.Model.Farm;
import com.Hinga.farmMis.Model.Farmer;
//import com.Hinga.farmMis.repository.FarmerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.Hinga.farmMis.repository")
@EntityScan(basePackages = "com.Hinga.farmMis.Model")
@EnableTransactionManagement
public class FarmMisApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmMisApplication.class, args);
	}

//	@Bean
//	CommandLineRunner commandLineRunner(FarmerRepository repo) {
//		return args -> {
//			// Create a Farm object
//			Address address=new Address("Gasabo","Mukamira","kamashashi","karandaryi");
//			Address homeAdd=new Address("Gasabo","Mukamira","mukenzi","kigarama");
////			List<Farm> farm = Collections.singletonList(new Farm("Kigali", 100, new Farmer("Aine", "400", address)),20);
//
//
////			 Create a Farmer object with a list of farms
//			Farmer farmer = new Farmer("Mugisha", "Kigali", homeAdd,20);
//			Farmer farmer2 = new Farmer("Ivan", "Kigali", address,18);
//
//
//
////			 Save the Farmer object to the repository
//			repo.save(farmer);
//
//			List<Farmer> farmers=new ArrayList<Farmer>();
//
//			farmers.add(farmer2);
//			repo.saveAll(farmers);
//
//			List<Farmer> savedFarmers=repo.findAll();
//			for (Farmer f: savedFarmers ){
//				System.out.println(f.getName() +f.getContact() );
//			}
//
////			Farmer f1= repo.getReferenceById(101);
////			f1.setName("Mugenzi arnauld");
////			repo.save(f1);
//
//
//
//
//		};
//	}
}
