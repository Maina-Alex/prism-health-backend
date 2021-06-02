package com.prismhealth.repository;

import java.util.List;

import com.prismhealth.Models.Services;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;

import org.springframework.data.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ServiceRepo extends PagingAndSortingRepository<Services, String> {

    List<Services> findAllByProviderPhone(String providerPhone);

    List<Services> findAll();

    List<Services> findAllByVerified(boolean verified, Sort sort);

    Slice<Services> findAllByVerified(boolean verified, Pageable pageable);

    public Slice<Services> findTop100ByVerifiedOrderByRatingDesc(boolean verified, Pageable pageable);

    List<Services> findByPositionNear(Point location, Distance distance);

    List<Services> findAllByLocationNameContainingIgnoreCase(String locationName);

    List<Services> findAllByNameContainingIgnoreCase(String name);

    List<Services> findAllByDescriptionContainingIgnoreCase(String description);

}
