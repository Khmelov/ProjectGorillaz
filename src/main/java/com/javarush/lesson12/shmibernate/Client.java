package com.javarush.lesson12.shmibernate;

import com.javarush.khmelov.dto.Role;
import com.javarush.khmelov.repository.Repository;

public class Client {
    public static void main(String[] args) {
        Repository<Developer> devRepo = PostgresRepository.get(Developer.class);
        Developer developer = Developer.builder()
                .name("test")
                .email("test@test.com")
                .phone("123456789")
                .role(Role.ADMIN)
                .build();
        devRepo.create(developer);
        System.out.printf("%ncreate  Developer: %s%n%n", developer);
        developer.setName("update");
        devRepo.update(developer);
        System.out.printf("%nupdate Developer: %s%n%n", developer);
        developer = devRepo.get(developer.getId());
        System.out.printf("%nget Developer: %s%n%n", developer);
        devRepo.delete(developer);
        System.out.printf("%nget Developer: %s%n%n", developer);
        Developer admin = Developer.builder().role(Role.ADMIN).build();
        devRepo.find(admin).forEach(System.out::println);
    }
}
