package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.ProfessionInputPort;
import co.edu.javeriana.as.personapp.application.port.out.ProfessionOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.ProfessionUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Profession;
import co.edu.javeriana.as.personapp.mapper.ProfesionMapperRest;
import co.edu.javeriana.as.personapp.model.request.ProfesionRequest;
import co.edu.javeriana.as.personapp.model.response.ProfesionResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterRest {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperRest profesionMapperRest;

	ProfessionInputPort professionInputPort;

	private String setOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
			return DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public List<ProfesionResponse> historial(String database) {
		log.info("Into historial ProfesionEntity in Input Adapter");
		try {
			setOutputPortInjection(database);
			String db = setOutputPortInjection(database);
			return professionInputPort.findAll().stream()
					.map(profession -> db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
						? profesionMapperRest.fromDomainToAdapterRestMaria(profession)
						: profesionMapperRest.fromDomainToAdapterRestMongo(profession))
					.collect(Collectors.toList());
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<ProfesionResponse>();
		}
	}

	public ProfesionResponse crearProfesion(ProfesionRequest request) {
		try {
			setOutputPortInjection(request.getDatabase());
			Profession profession = professionInputPort.create(profesionMapperRest.fromAdapterToDomain(request));
			String db = setOutputPortInjection(request.getDatabase());
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? profesionMapperRest.fromDomainToAdapterRestMaria(profession)
				: profesionMapperRest.fromDomainToAdapterRestMongo(profession);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse("", "", "", request.getDatabase(), "ERROR: " + e.getMessage());
		}
	}

	public ProfesionResponse obtenerProfesionPorId(String database, Integer id) {
		try {
			setOutputPortInjection(database);
			Profession profession = professionInputPort.findOne(id);
			String db = setOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? profesionMapperRest.fromDomainToAdapterRestMaria(profession)
				: profesionMapperRest.fromDomainToAdapterRestMongo(profession);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse("", "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse("", "", "", database, "ERROR: " + e.getMessage());
		}
	}

	public ProfesionResponse actualizarProfesion(String database, Integer id, ProfesionRequest request) {
		try {
			setOutputPortInjection(database);
			Profession profession = profesionMapperRest.fromAdapterToDomain(request);
			profession.setIdentification(id); // Asegurar que el ID del path coincida
			Profession updatedProfession = professionInputPort.edit(id, profession);
			String db = setOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? profesionMapperRest.fromDomainToAdapterRestMaria(updatedProfession)
				: profesionMapperRest.fromDomainToAdapterRestMongo(updatedProfession);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse("", "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse("", "", "", database, "ERROR: " + e.getMessage());
		}
	}

	public ProfesionResponse eliminarProfesion(String database, Integer id) {
		try {
			setOutputPortInjection(database);
			Boolean deleted = professionInputPort.drop(id);
			if (deleted) {
				return new ProfesionResponse(id.toString(), "", "", database, "DELETED");
			} else {
				return new ProfesionResponse(id.toString(), "", "", database, "ERROR: No se pudo eliminar");
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse(id.toString(), "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new ProfesionResponse(id.toString(), "", "", database, "ERROR: " + e.getMessage());
		}
	}

}

