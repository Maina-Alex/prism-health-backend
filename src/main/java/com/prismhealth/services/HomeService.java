package com.prismhealth.services;
/*
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mgari.mgaribackend.domain.Car;
import com.mgari.mgaribackend.domain.CarDocuments;
import com.mgari.mgaribackend.domain.UserIdentification;
import com.mgari.mgaribackend.domain.UserLocation;
import com.mgari.mgaribackend.domain.Users;
import com.mgari.mgaribackend.repo.CarRepo;
import com.mgari.mgaribackend.repo.UsersRepo;
import com.mgari.mgaribackend.services.booking.CarBookingService;
import com.mgari.mgaribackend.services.car.CarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.geo.Distance;

import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    @Autowired
    private CarRepo carRepo;
    @Autowired
    private CarBookingService carBookingService;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private CarService carService;

    public List<Car> getNearByCars(UserLocation location) {
        Point point = new Point(location.getPosition()[0], location.getPosition()[1]);
        Distance distance = new Distance(location.getRadius(), Metrics.KILOMETERS);
        return carRepo.findByPositionNear(point, distance).stream().filter(Car::isVerified).map(c -> {
            Optional<Users> owner = usersRepo.findById(c.getOwnerId());
            if (owner.isPresent())
                c.setCarOwner(owner.get());
            c.setBookings(carBookingService.getCarBookings(c.getId()));
            c.setRating(carService.getCarRating(c.getId()).get("rating"));
            c.setRatingsCount(carService.getCarRating(c.getId()).get("count"));
            this.sanitizeCar(c);
            return c;
        }).collect(Collectors.toList());

    }

    public Slice<Car> getPopularCars(int size, int page) {
        carRepo.findAll().stream().map(c -> {
            c.setRating(carService.getCarRating(c.getId()).get("rating"));
            c.setRatingsCount(carService.getCarRating(c.getId()).get("count"));
            return c;

        }).forEach(c -> carRepo.save(c));

        Slice<Car> cars = carRepo.findTop100ByVerifiedOrderByRatingDesc(true, PageRequest.of(page, size));
        cars.getContent()

                .forEach(c -> {
                    c.setBookings(carBookingService.getCarBookings(c.getId()));
                    Optional<Users> owner = usersRepo.findById(c.getOwnerId());
                    if (owner.isPresent())
                        c.setCarOwner(owner.get());
                    this.sanitizeCar(c);

                });

        return cars;

    }

    public void sanitizeCar(Car car) {
        Optional<Users> owner = usersRepo.findById(car.getOwnerId());
        if (owner.isPresent()) {
            car.getCarOwner().setIdentification(new UserIdentification());
            car.setDocuments(new CarDocuments());
        }

    }

}*/
