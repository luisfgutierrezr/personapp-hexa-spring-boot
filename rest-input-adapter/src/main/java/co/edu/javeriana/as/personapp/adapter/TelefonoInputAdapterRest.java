package co.edu.javeriana.as.personapp.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import co.edu.javeriana.as.personapp.application.port.in.PersonInputPort;
import co.edu.javeriana.as.personapp.application.port.in.PhoneInputPort;
import co.edu.javeriana.as.personapp.application.port.out.PersonOutputPort;
import co.edu.javeriana.as.personapp.application.port.out.PhoneOutputPort;
import co.edu.javeriana.as.personapp.application.usecase.PersonUseCase;
import co.edu.javeriana.as.personapp.application.usecase.PhoneUseCase;
import co.edu.javeriana.as.personapp.common.annotations.Adapter;
import co.edu.javeriana.as.personapp.common.exceptions.InvalidOptionException;
import co.edu.javeriana.as.personapp.common.exceptions.NoExistException;
import co.edu.javeriana.as.personapp.common.setup.DatabaseOption;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mapper.TelefonoMapperRest;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterRest {

	@Autowired
	@Qualifier("personOutputAdapterMaria")
	private PersonOutputPort personOutputPortMaria;

	@Autowired
	@Qualifier("personOutputAdapterMongo")
	private PersonOutputPort personOutputPortMongo;

	@Autowired
	@Qualifier("phoneOutputAdapterMaria")
	private PhoneOutputPort phoneOutputPortMaria;

	@Autowired
	@Qualifier("phoneOutputAdapterMongo")
	private PhoneOutputPort phoneOutputPortMongo;

	@Autowired
	private TelefonoMapperRest telefonoMapperRest;

	PersonInputPort personInputPort;
	PhoneInputPort phoneInputPort;

	private String setOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			phoneInputPort = new PhoneUseCase(phoneOutputPortMaria);
			return DatabaseOption.MARIA.toString();
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			phoneInputPort = new PhoneUseCase(phoneOutputPortMongo);
			return DatabaseOption.MONGO.toString();
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public List<TelefonoResponse> historial(String database) {
		log.info("Into historial TelefonoEntity in Input Adapter");
		try {
			setOutputPortInjection(database);
			String db = setOutputPortInjection(database);
			return phoneInputPort.findAll().stream()
					.map(phone -> db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
						? telefonoMapperRest.fromDomainToAdapterRestMaria(phone)
						: telefonoMapperRest.fromDomainToAdapterRestMongo(phone))
					.collect(Collectors.toList());
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new ArrayList<TelefonoResponse>();
		}
	}

	public TelefonoResponse crearTelefono(TelefonoRequest request) {
		try {
			setOutputPortInjection(request.getDatabase());
			Person owner = personInputPort.findOne(request.getOwnerId());
			if (owner == null) {
				return new TelefonoResponse("", "", null, request.getDatabase(), "ERROR: Persona no encontrada");
			}
			Phone phone = phoneInputPort.create(telefonoMapperRest.fromAdapterToDomain(request, owner));
			String db = setOutputPortInjection(request.getDatabase());
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? telefonoMapperRest.fromDomainToAdapterRestMaria(phone)
				: telefonoMapperRest.fromDomainToAdapterRestMongo(phone);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse("", "", null, request.getDatabase(), "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse("", "", null, request.getDatabase(), "ERROR: " + e.getMessage());
		}
	}

	public TelefonoResponse obtenerTelefonoPorId(String database, String number) {
		try {
			setOutputPortInjection(database);
			Phone phone = phoneInputPort.findOne(number);
			String db = setOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? telefonoMapperRest.fromDomainToAdapterRestMaria(phone)
				: telefonoMapperRest.fromDomainToAdapterRestMongo(phone);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse("", "", null, database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse("", "", null, database, "ERROR: " + e.getMessage());
		}
	}

	public TelefonoResponse actualizarTelefono(String database, String number, TelefonoRequest request) {
		try {
			setOutputPortInjection(database);
			Person owner = personInputPort.findOne(request.getOwnerId());
			if (owner == null) {
				return new TelefonoResponse("", "", null, database, "ERROR: Persona no encontrada");
			}
			Phone phone = telefonoMapperRest.fromAdapterToDomain(request, owner);
			phone.setNumber(number); // Asegurar que el número del path coincida
			Phone updatedPhone = phoneInputPort.edit(number, phone);
			String db = setOutputPortInjection(database);
			return db.equalsIgnoreCase(DatabaseOption.MARIA.toString()) 
				? telefonoMapperRest.fromDomainToAdapterRestMaria(updatedPhone)
				: telefonoMapperRest.fromDomainToAdapterRestMongo(updatedPhone);
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse("", "", null, database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse("", "", null, database, "ERROR: " + e.getMessage());
		}
	}

	public TelefonoResponse eliminarTelefono(String database, String number) {
		try {
			setOutputPortInjection(database);
			Boolean deleted = phoneInputPort.drop(number);
			if (deleted) {
				return new TelefonoResponse(number, "", null, database, "DELETED");
			} else {
				return new TelefonoResponse(number, "", null, database, "ERROR: No se pudo eliminar");
			}
		} catch (InvalidOptionException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse(number, "", null, database, "ERROR: " + e.getMessage());
		} catch (NoExistException e) {
			log.warn(e.getMessage());
			return new TelefonoResponse(number, "", null, database, "ERROR: " + e.getMessage());
		}
	}

}

