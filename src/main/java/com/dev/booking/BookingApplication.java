package com.dev.booking;

import com.dev.booking.TestEvent.MyDog;
import com.dev.booking.TestEvent.MyHouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BookingApplication {

	@Autowired
	MyHouse myHouse;
	@Autowired
	MyDog myDog;

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

//	@Bean
//	CommandLineRunner run() {
//		return args -> {
//			System.out.println(Thread.currentThread().getName() + ": Người đầu tiên bấm chuông");
//			myHouse.rangDoorbellBy("Người đầu tiên");
//
//			Thread.sleep( 60 * 1000); // 4 phút
//			System.out.println(Thread.currentThread().getName() + ": Người thứ hai bấm chuông");
//			myHouse.rangDoorbellBy("Người thứ hai");
//
//			Thread.sleep( 60 * 1000); // 4 phút
//			System.out.println(Thread.currentThread().getName() + ": Người thứ ba bấm chuông");
//			myHouse.rangDoorbellBy("Người thứ ba");
//
//			// Hủy sự kiện của người thứ hai sau 10 phút
//			Thread.sleep( 60 * 1000); // 2 phút (tổng cộng 10 phút từ khi người thứ hai bấm chuông)
//			myDog.cancelEvent("Người thứ hai");
//		};
//	}
}
