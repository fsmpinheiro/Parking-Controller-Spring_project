package com.apiproject.parkingcontroller.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.apiproject.parkingcontroller.models.ParkingSpotModel;
import com.apiproject.parkingcontroller.repositories.ParkingSpotRepository;

import jakarta.transaction.Transactional;

@Service
public class ParkingSpotService {

	/** Criando injeção de dependência via anotação
	@Autowired
	ParkingSpotRepository parkingSpotRepository;
	**/
	
	/** Criando injeção de dependência via construtor **/
	final ParkingSpotRepository parkingSpotRepository;
	
	public ParkingSpotService( ParkingSpotRepository parkingSpotRepository ) {
		this.parkingSpotRepository = parkingSpotRepository;
	}

	
	
	@Transactional
	public ParkingSpotModel save( ParkingSpotModel parkingSpotModel ) {
		return parkingSpotRepository.save( parkingSpotModel );
	}

	@Transactional
	public void deleteSpot(ParkingSpotModel parkingSpotModel) {
		parkingSpotRepository.delete( parkingSpotModel );	
	}

	public boolean existsByLicensePlateCar(String licensePlateCar) {
		return parkingSpotRepository.existsByLicensePlateCar( licensePlateCar );
	}

	public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
		return parkingSpotRepository.existsByParkingSpotNumber( parkingSpotNumber );
	}

	public boolean existsByApartmentAndBlock(String apartment, String block) {
		return parkingSpotRepository.existsByApartmentAndBlock( apartment, block );
	}
	
	public Page<ParkingSpotModel> findAll( Pageable pageable){
		return parkingSpotRepository.findAll( pageable );
	}

	public Optional<ParkingSpotModel> findById(UUID id) {
		return parkingSpotRepository.findById( id );
	}
	
		
}