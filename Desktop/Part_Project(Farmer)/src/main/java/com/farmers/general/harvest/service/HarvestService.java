package com.farmers.general.harvest.service;

import com.farmers.general.exception.ResourceNotFoundException;
import com.farmers.general.harvest.dto.HarvestRequest;
import com.farmers.general.harvest.dto.HarvestResponse;
import com.farmers.general.harvest.entity.Harvest;
import com.farmers.general.harvest.repository.HarvestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HarvestService {

    private final HarvestRepository harvestRepository;

    public List<HarvestResponse> getHarvests(Long farmId) {
        return harvestRepository.findByFarmIdAndDeletedFalseOrderByHarvestDateDesc(farmId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public HarvestResponse getHarvestById(Long id) {
        Harvest harvest = harvestRepository.findById(id)
                .filter(h -> !h.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", id));
        return toResponse(harvest);
    }

    @Transactional
    public HarvestResponse createHarvest(Long farmId, Long userId, HarvestRequest request) {
        Harvest harvest = Harvest.builder()
                .farmId(farmId)
                .fieldId(request.getFieldId())
                .cropId(request.getCropId())
                .recordedBy(userId)
                .harvestDate(request.getHarvestDate())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .quality(request.getQuality())
                .estimatedValue(request.getEstimatedValue())
                .actualRevenue(request.getActualRevenue())
                .buyerId(request.getBuyerId())
                .notes(request.getNotes())
                .imageUrl(request.getImageUrl())
                .build();

        harvest = harvestRepository.save(harvest);
        return toResponse(harvest);
    }

    @Transactional
    public HarvestResponse updateHarvest(Long id, HarvestRequest request) {
        Harvest harvest = harvestRepository.findById(id)
                .filter(h -> !h.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", id));

        harvest.setFieldId(request.getFieldId());
        harvest.setCropId(request.getCropId());
        harvest.setHarvestDate(request.getHarvestDate());
        harvest.setQuantity(request.getQuantity());
        harvest.setUnit(request.getUnit());
        harvest.setQuality(request.getQuality());
        harvest.setEstimatedValue(request.getEstimatedValue());
        harvest.setActualRevenue(request.getActualRevenue());
        harvest.setBuyerId(request.getBuyerId());
        harvest.setNotes(request.getNotes());
        harvest.setImageUrl(request.getImageUrl());

        harvest = harvestRepository.save(harvest);
        return toResponse(harvest);
    }

    @Transactional
    public void deleteHarvest(Long id) {
        Harvest harvest = harvestRepository.findById(id)
                .filter(h -> !h.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Harvest", id));
        harvest.setDeleted(true);
        harvestRepository.save(harvest);
    }

    private HarvestResponse toResponse(Harvest harvest) {
        HarvestResponse response = new HarvestResponse();
        response.setId(harvest.getId());
        response.setFarmId(harvest.getFarmId());
        response.setFieldId(harvest.getFieldId());
        response.setCropId(harvest.getCropId());
        response.setRecordedBy(harvest.getRecordedBy());
        response.setHarvestDate(harvest.getHarvestDate());
        response.setQuantity(harvest.getQuantity());
        response.setUnit(harvest.getUnit());
        response.setQuality(harvest.getQuality());
        response.setEstimatedValue(harvest.getEstimatedValue());
        response.setActualRevenue(harvest.getActualRevenue());
        response.setBuyerId(harvest.getBuyerId());
        response.setNotes(harvest.getNotes());
        response.setImageUrl(harvest.getImageUrl());
        response.setCreatedAt(harvest.getCreatedAt());
        response.setUpdatedAt(harvest.getUpdatedAt());
        return response;
    }
}
