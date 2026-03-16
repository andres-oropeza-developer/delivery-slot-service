package com.walmart.deliveryslot.application.service;

import com.walmart.deliveryslot.application.dto.response.CommuneResponse;
import com.walmart.deliveryslot.application.exception.ResourceNotFoundException;
import com.walmart.deliveryslot.domain.model.Commune;
import com.walmart.deliveryslot.domain.model.Zone;
import com.walmart.deliveryslot.domain.repository.CommuneRepository;
import com.walmart.deliveryslot.domain.repository.ZoneRepository;
import com.walmart.deliveryslot.infrastructure.persistence.entity.CommuneEntity;
import com.walmart.deliveryslot.infrastructure.persistence.entity.RegionEntity;
import com.walmart.deliveryslot.infrastructure.persistence.entity.ZoneEntity;
import com.walmart.deliveryslot.infrastructure.persistence.repository.CommuneJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommuneService")
class CommuneServiceTest {

    @Mock private CommuneRepository communeRepository;
    @Mock private ZoneRepository zoneRepository;
    @Mock private CommuneJpaRepository communeJpaRepository;

    @InjectMocks private CommuneService communeService;

    private CommuneEntity communeEntity;

    @BeforeEach
    void setUp() {
        ZoneEntity zoneEntity = new ZoneEntity();
        zoneEntity.setId("z-rm-oriente");
        zoneEntity.setName("RM Oriente");
        zoneEntity.setActive(true);

        RegionEntity regionEntity = new RegionEntity();
        regionEntity.setId("r-07");
        regionEntity.setName("Región Metropolitana");

        communeEntity = new CommuneEntity();
        communeEntity.setId("c-013");
        communeEntity.setName("Providencia");
        communeEntity.setZone(zoneEntity);
        communeEntity.setRegion(regionEntity);
    }

    @Test
    @DisplayName("search retorna resultados cuando query tiene 2+ caracteres")
    void search_returnsResults() {
        when(communeJpaRepository.searchByName("prov")).thenReturn(List.of(communeEntity));

        List<CommuneResponse> results = communeService.search("prov");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Providencia");
        assertThat(results.get(0).zoneName()).isEqualTo("RM Oriente");
        assertThat(results.get(0).regionName()).isEqualTo("Región Metropolitana");
    }

    @Test
    @DisplayName("search retorna lista vacía cuando query tiene menos de 2 caracteres")
    void search_returnsEmpty_whenQueryTooShort() {
        List<CommuneResponse> r1 = communeService.search("p");
        List<CommuneResponse> r2 = communeService.search("");
        List<CommuneResponse> r3 = communeService.search(null);

        assertThat(r1).isEmpty();
        assertThat(r2).isEmpty();
        assertThat(r3).isEmpty();
        verify(communeJpaRepository, never()).searchByName(any());
    }

    @Test
    @DisplayName("search hace trim del query antes de buscar")
    void search_trimsQuery() {
        when(communeJpaRepository.searchByName("prov")).thenReturn(List.of());

        communeService.search("  prov  ");

        verify(communeJpaRepository).searchByName("prov");
    }

    @Test
    @DisplayName("findById retorna la comuna correctamente")
    void findById_returnsCommune() {
        Commune commune = new Commune("c-013", "r-07", "z-rm-oriente", "Providencia");
        Zone zone = new Zone("z-rm-oriente", "r-07", "RM Oriente", "desc", true);
        when(communeRepository.findById("c-013")).thenReturn(Optional.of(commune));
        when(zoneRepository.findById("z-rm-oriente")).thenReturn(Optional.of(zone));

        CommuneResponse result = communeService.findById("c-013");

        assertThat(result.id()).isEqualTo("c-013");
        assertThat(result.name()).isEqualTo("Providencia");
        assertThat(result.zoneName()).isEqualTo("RM Oriente");
    }

    @Test
    @DisplayName("findById lanza ResourceNotFoundException cuando no existe")
    void findById_throwsNotFound() {
        when(communeRepository.findById("c-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> communeService.findById("c-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("c-999");
    }
}
