package com.apiproject.parkingcontroller.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.apiproject.parkingcontroller.dtos.ParkingSpotDto;
import com.apiproject.parkingcontroller.models.ParkingSpotModel;
import com.apiproject.parkingcontroller.services.ParkingSpotService;

import jakarta.validation.Valid;


@RestController
@CrossOrigin( origins = "*", maxAge = 3600 )
@RequestMapping( "/parking-spot")
public class ParkingSpotController {

	final ParkingSpotService parkingSpotService;

	public ParkingSpotController( ParkingSpotService parkingSpotService ) {
		this.parkingSpotService = parkingSpotService;
	}
	
	
	@PostMapping
	public ResponseEntity<Object> saveParkingSpot( @RequestBody @Valid ParkingSpotDto parkingSpotDto ){
		
		if( parkingSpotService.existsByLicensePlateCar( parkingSpotDto.getLicensePlateCar( ) ) ) {
			return ResponseEntity.
					status( HttpStatus.CONFLICT ).
					body("Conflict: Placa já em uso em outra vaga");
		}
		
		if( parkingSpotService.existsByParkingSpotNumber( parkingSpotDto.getParkingSpotNumber( ) ) ) {
			return ResponseEntity.
					status( HttpStatus.CONFLICT ).
					body( "Conflict: Vaga de estacionamento já em uso" );
		}
		
		if( parkingSpotService.existsByApartmentAndBlock( parkingSpotDto.getApartment( ), parkingSpotDto.getBlock( ) ) ) {
			return ResponseEntity.
					status( HttpStatus.CONFLICT ).
					body( "Conflict: Vaga já registrada para outro apartamento" );
		}
		
		var parkingSpotModel = new ParkingSpotModel( );
		BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel );
		parkingSpotModel.setRegistrationDate( LocalDateTime.now( ZoneId.of("UTC") ) );
		
		return ResponseEntity.status( HttpStatus.CREATED ).body( parkingSpotService.save( parkingSpotModel ) ); 
	}
	
	@GetMapping
	public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(
									@PageableDefault(page = 0, size = 10, sort = "id", 
													 direction = Sort.Direction.ASC) Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK ).body( parkingSpotService.findAll( pageable ) );
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getOneParkingSpot( @PathVariable( value= "id" ) UUID id){
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById( id );
		if( !parkingSpotModelOptional.isPresent( ) ) {
			return ResponseEntity.status( HttpStatus.NOT_FOUND ).body("Vaga não encontrada");
		}
		return ResponseEntity.status( HttpStatus.OK ).body( parkingSpotModelOptional.get( ) );
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteParkingSpot( @PathVariable( value= "id") UUID id ){
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById( id );
		if( !parkingSpotModelOptional.isPresent( ) ) {
			return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Vaga não encontrada" );
		}
		
		parkingSpotService.deleteSpot( parkingSpotModelOptional.get( ) );
		return ResponseEntity.status( HttpStatus.OK ).body( "Vaga deletada com sucesso..." );
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateParkingSpot( @PathVariable( value= "id" ) UUID id, 
													 @RequestBody @Valid ParkingSpotDto parkingSpotDto ){
		
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById( id );
		if( !parkingSpotModelOptional.isPresent( ) ) {
			return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( "Vaga não encontrada" );
		}
		
		var parkingSpotModel = new ParkingSpotModel( );
		BeanUtils.copyProperties( parkingSpotDto, parkingSpotModel );
		parkingSpotModel.setId( parkingSpotModelOptional.get( ).getId( ) );
		parkingSpotModel.setRegistrationDate( parkingSpotModelOptional.get( ).getRegistrationDate( ) );
		
		/**
		var parkingSpotModel = parkingSpotModelOptional.get( );
		
		parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber( ) );
		parkingSpotModel.setLicensePlateCar(parkingSpotDto.getLicensePlateCar( ) );
		parkingSpotModel.setModelCar(parkingSpotDto.getModelCar( ) );
		parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar( ) );
		parkingSpotModel.setColorCar(parkingSpotDto.getColorCar( ) );
		parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName( ) );
		parkingSpotModel.setApartment(parkingSpotDto.getApartment( ) );
		parkingSpotModel.setBlock(parkingSpotDto.getBlock( ) );
		**/

		return ResponseEntity.status( HttpStatus.OK ).body( parkingSpotService.save( parkingSpotModel ) );
	}
}
