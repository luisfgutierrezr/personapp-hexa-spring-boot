package co.edu.javeriana.as.personapp.terminal.adapter;

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
import co.edu.javeriana.as.personapp.terminal.mapper.ProfesionMapperCli;
import co.edu.javeriana.as.personapp.terminal.model.ProfesionModelCli;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Adapter
public class ProfesionInputAdapterCli {

	@Autowired
	@Qualifier("professionOutputAdapterMaria")
	private ProfessionOutputPort professionOutputPortMaria;

	@Autowired
	@Qualifier("professionOutputAdapterMongo")
	private ProfessionOutputPort professionOutputPortMongo;

	@Autowired
	private ProfesionMapperCli profesionMapperCli;

	ProfessionInputPort professionInputPort;

	public void setProfessionOutputPortInjection(String dbOption) throws InvalidOptionException {
		if (dbOption.equalsIgnoreCase(DatabaseOption.MARIA.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMaria);
		} else if (dbOption.equalsIgnoreCase(DatabaseOption.MONGO.toString())) {
			professionInputPort = new ProfessionUseCase(professionOutputPortMongo);
		} else {
			throw new InvalidOptionException("Invalid database option: " + dbOption);
		}
	}

	public void historial() {
		log.info("Into historial ProfesionEntity in Input Adapter");
		professionInputPort.findAll().stream()
			.map(profesionMapperCli::fromDomainToAdapterCli)
			.forEach(System.out::println);
	}

	public Profession crearProfesion(Profession profession) {
		log.info("Into crearProfesion in Input Adapter");
		return professionInputPort.create(profession);
	}

	public Profession editarProfesion(Integer identification, Profession profession) {
		log.info("Into editarProfesion in Input Adapter");
		try {
			return professionInputPort.edit(identification, profession);
		} catch (NoExistException e) {
			log.warn("Profession does not exist: " + e.getMessage());
			return null;
		}
	}

	public Boolean eliminarProfesion(Integer identification) {
		log.info("Into eliminarProfesion in Input Adapter");
		try {
			return professionInputPort.drop(identification);
		} catch (NoExistException e) {
			log.warn("Profession does not exist: " + e.getMessage());
			return false;
		}
	}

	public ProfesionModelCli buscarProfesion(Integer identification) {
		log.info("Into buscarProfesion in Input Adapter");
		try {
			Profession profession = professionInputPort.findOne(identification);
			return profession != null ? profesionMapperCli.fromDomainToAdapterCli(profession) : null;
		} catch (NoExistException e) {
			log.warn("Profession not found: " + e.getMessage());
			return null;
		}
	}

	public List<ProfesionModelCli> buscarTodas() {
		log.info("Into buscarTodas ProfesionEntity in Input Adapter");
		return professionInputPort.findAll().stream()
			.map(profesionMapperCli::fromDomainToAdapterCli)
			.collect(Collectors.toList());
	}

	public Integer contarProfesiones() {
		log.info("Into contarProfesiones in Input Adapter");
		return professionInputPort.count();
	}
}

