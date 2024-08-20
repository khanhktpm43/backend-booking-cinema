package com.dev.booking;

//import com.dev.booking.TestEvent.MyDog;
//import com.dev.booking.TestEvent.MyHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BookingApplication {


	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

}
