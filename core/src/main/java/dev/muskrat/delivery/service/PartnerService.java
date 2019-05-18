package dev.muskrat.delivery.service;

import dev.muskrat.delivery.dto.PartnerDTO;
import dev.muskrat.delivery.dto.PartnerRegisterDTO;
import dev.muskrat.delivery.dto.PartnerRegisterResponseDTO;

import java.util.List;
import java.util.Optional;

public interface PartnerService {

    PartnerRegisterResponseDTO create(PartnerRegisterDTO partnerDTO);

    void update(PartnerDTO partnerDTO);

    void delete(PartnerDTO partnerDTO);

    Optional<PartnerDTO> findById(long id);

    List<PartnerDTO> findAll();

}
