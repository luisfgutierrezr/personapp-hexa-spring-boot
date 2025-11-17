package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.terminal.mapper.TelefonoMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class TelefonoInputAdapterCli {

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
	private TelefonoMapperCli telefonoMapperCli;

	PersonInputPort personInputPort;
	PhoneInputPort phoneInputPort;

	public void setPhoneOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMaria);
			phoneInputPort = new PhoneUseCase(phoneOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			personInputPort = new PersonUseCase(personOutputPortMongo);
			phoneInputPort = new PhoneUseCase(phoneOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		log.info("Into historial TelefonoEntity in Input Adapter");
		phoneInputPort.findAll().stream()
			.map(telefonoMapperCli::fromDomainToAdapterCli)
			.forEach(System.out::println);
	}

	public Phone crearTelefono(Phone phone) {
		log.info("Into crearTelefono in Input Adapter");
		try {
			// Validate that the owner exists
			if (phone.getOwner() != null && phone.getOwner().getIdentification() != null) {
				Person owner = personInputPort.findOne(phone.getOwner().getIdentification());
				if (owner == null) {
					log.warn("Owner with identification " + phone.getOwner().getIdentification() + " does not exist");
					return null;
				}
				phone.setOwner(owner);
			}
			return phoneInputPort.create(phone);
		} catch (NoExistException e) {
			log.warn("Owner does not exist: " + e.getMessage());
			return null;
		}
	}

	public Phone editarTelefono(String number, Phone phone) {
		log.info("Into editarTelefono in Input Adapter");
		try {
			// Validate that the owner exists
			if (phone.getOwner() != null && phone.getOwner().getIdentification() != null) {
				Person owner = personInputPort.findOne(phone.getOwner().getIdentification());
				if (owner == null) {
					log.warn("Owner with identification " + phone.getOwner().getIdentification() + " does not exist");
					return null;
				}
				phone.setOwner(owner);
			}
			return phoneInputPort.edit(number, phone);
		} catch (NoExistException e) {
			log.warn("Error editing phone: " + e.getMessage());
			return null;
		}
	}

	public Boolean eliminarTelefono(String number) {
		log.info("Into eliminarTelefono in Input Adapter");
		try {
			return phoneInputPort.drop(number);
		} catch (NoExistException e) {
			log.warn("Phone does not exist: " + e.getMessage());
			return false;
		}
	}

	public TelefonoModelCli buscarTelefono(String number) {
		log.info("Into buscarTelefono in Input Adapter");
		try {
			Phone phone = phoneInputPort.findOne(number);
			return phone != null ? telefonoMapperCli.fromDomainToAdapterCli(phone) : null;
		} catch (NoExistException e) {
			log.warn("Phone not found: " + e.getMessage());
			return null;
		}
	}

	public List<TelefonoModelCli> buscarTodos() {
		log.info("Into buscarTodos TelefonoEntity in Input Adapter");
		return phoneInputPort.findAll().stream()
			.map(telefonoMapperCli::fromDomainToAdapterCli)
			.collect(Collectors.toList());
	}

	public Integer contarTelefonos() {
		log.info("Into contarTelefonos in Input Adapter");
		return phoneInputPort.count();
	}
}

