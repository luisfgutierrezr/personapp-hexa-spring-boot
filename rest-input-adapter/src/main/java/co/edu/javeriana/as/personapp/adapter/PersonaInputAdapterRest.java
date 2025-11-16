package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.mapper.PersonaMapperRest;
import co.edu.javeriana.as.personapp.model.request.PersonaRequest;
import co.edu.javeriana.as.personapp.model.response.PersonaResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class PersonaInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	private PersonaMapperRest personaMapperRest;

	PersonInputPort personInputPort;

	private String setPersonOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			return  DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public List<PersonaResponse> historial(String database) {
		log.info("Into historial PersonaEntity in Input Adapter");
		try {
			if(setPersonOutputPortInjection(database).equalsIgnoreCase(DatabaseOption.MARIA.toString())){
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMaria)
						.collect(Collectors.toList());
			}else {
				return personInputPort.findAll().stream().map(personaMapperRest::fromDomainToAdapterRestMongo)
						.collect(Collectors.toList());
			}
			
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<PersonaResponse>();
		}
	}

	public PersonaResponse crearPersona(PersonaRequest request) {
		try {
			setPersonOutputPortInjection(request.getDatabase());
			Person person = personInputPort.create(personaMapperRest.fromAdapterToDomain(request));
			String database = setPersonOutputPortInjection(request.getDatabase());
			return database.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? personaMapperRest.fromDomainToAdapterRestMaria(person)
				: personaMapperRest.fromDomainToAdapterRestMongo(person);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new PersonaResponse("", "", "", "", "", "", "ERROR: " + e.getMessage());
		}
	}

	public PersonaResponse obtenerPersonaPorId(String database, Integer id) {
		log.info("obtenerPersonaPorId - Iniciando búsqueda de persona con ID: {} en base de datos: {}", id, database);
		try {
			String db = setPersonOutputPortInjection(database);
			log.info("obtenerPersonaPorId - Base de datos configurada: {}", db);
			Person person = personInputPort.findOne(id);
			log.info("obtenerPersonaPorId - Persona encontrada: {}", person != null ? person.getIdentification() : "null");
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? personaMapperRest.fromDomainToAdapterRestMaria(person)
				: personaMapperRest.fromDomainToAdapterRestMongo(person);
		} catch (NoExistException e) {
			log.warn("obtenerPersonaPorId - Persona no encontrada: {}", e.getMessage());
			return new PersonaResponse("", "", "", "", "", database, "ERROR: " + e.getMessage());
		} catch (InvalidOptionException e) {
			log.warn("obtenerPersonaPorId - Opción de base de datos inválida: {}", e.getMessage());
			return new PersonaResponse("", "", "", "", "", database, "ERROR: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn("obtenerPersonaPorId - Argumento inválido: {}", e.getMessage());
			return new PersonaResponse("", "", "", "", "", database, "ERROR: " + e.getMessage());
		} catch (Exception e) {
			log.error("obtenerPersonaPorId - Error inesperado al obtener persona con ID: {}", id, e);
			return new PersonaResponse("", "", "", "", "", database, "ERROR: " + e.getMessage());
		}
	}

	public PersonaResponse actualizarPersona(String database, Integer id, PersonaRequest request) {
		try {
			setPersonOutputPortInjection(database);
			Person person = personaMapperRest.fromAdapterToDomain(request);
			person.setIdentification(id); // Asegurar que el ID del path coincida
			Person updatedPerson = personInputPort.edit(id, person);
			String db = setPersonOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? personaMapperRest.fromDomainToAdapterRestMaria(updatedPerson)
				: personaMapperRest.fromDomainToAdapterRestMongo(updatedPerson);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new PersonaResponse("", "", "", "", "", "", "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new PersonaResponse("", "", "", "", "", "", "ERROR: " + e.getMessage());
		}
	}

	public PersonaResponse eliminarPersona(String database, Integer id) {
		try {
			setPersonOutputPortInjection(database);
			Boolean deleted = personInputPort.drop(id);
			if (deleted) {
				return new PersonaResponse(id.toString(), "", "", "", "", database, "DELETED");
			} else {
				return new PersonaResponse(id.toString(), "", "", "", "", database, "ERROR: No se pudo eliminar");
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new PersonaResponse(id.toString(), "", "", "", "", database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new PersonaResponse(id.toString(), "", "", "", "", database, "ERROR: " + e.getMessage());
		}
	}

}
