package co.edu.javeriana.as.personapp.terminal.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.terminal.model.TelefonoModelCli;

@Mapper
public class TelefonoMapperCli {

	@Autowired
	private PersonaMapperCli personaMapperCli;

	public TelefonoModelCli fromDomainToAdapterCli(Phone phone) {
		TelefonoModelCli telefonoModelCli = new TelefonoModelCli();
		telefonoModelCli.setNum(phone.getNumber());
		telefonoModelCli.setOper(phone.getCompany());
		telefonoModelCli.setDuenio(phone.getOwner() != null ? personaMapperCli.fromDomainToAdapterCli(phone.getOwner()) : null);
		return telefonoModelCli;
	}
}

