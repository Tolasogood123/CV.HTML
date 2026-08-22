package com.farmers.general.harvest.repository;

import com.farmers.general.harvest.entity.Harvest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HarvestRepository extends JpaRepository<Harvest, Long> {

    List<Harvest> findByFarmIdAndDeletedFalseOrderByHarvestDateDesc(Long farmId);

    List<Harvest> findByFarmIdAndCropIdAndDeletedFalse(Long farmId, Long cropId);

    List<Harvest> findByFarmIdAndFieldIdAndDeletedFalse(Long farmId, Long fieldId);
}
