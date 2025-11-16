package co.edu.javeriana.as.personapp.mariadb.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mariadb.entity.PersonaEntity;
import co.edu.javeriana.as.personapp.mariadb.entity.TelefonoEntity;
import lombok.NonNull;

@Mapper
public class TelefonoMapperMaria {

	@Autowired
	private PersonaMapperMaria personaMapperMaria;

	public TelefonoEntity fromDomainToAdapter(Phone phone) {
		TelefonoEntity telefonoEntity = new TelefonoEntity();
		telefonoEntity.setNum(phone.getNumber());
		telefonoEntity.setOper(phone.getCompany());
		telefonoEntity.setDuenio(validateDuenio(phone.getOwner()));
		return telefonoEntity;
	}

	private PersonaEntity validateDuenio(@NonNull Person owner) {
		// Solo establecer la referencia por ID para evitar referencias circulares
		if (owner != null && owner.getIdentification() != null) {
			PersonaEntity personaEntity = new PersonaEntity();
			personaEntity.setCc(owner.getIdentification());
			return personaEntity;
		}
		return new PersonaEntity();
	}

	public Phone fromAdapterToDomain(TelefonoEntity telefonoEntity) {
		Phone phone = new Phone();
		phone.setNumber(telefonoEntity.getNum());
		phone.setCompany(telefonoEntity.getOper());
		phone.setOwner(validateOwner(telefonoEntity.getDuenio()));
		return phone;
	}

	private @NonNull Person validateOwner(PersonaEntity duenio) {
		// Solo crear una referencia mínima con el ID para evitar referencias circulares
		// NO mapear la persona completa porque causaría un ciclo infinito
		if (duenio != null && duenio.getCc() != null) {
			Person person = new Person();
			person.setIdentification(duenio.getCc());
			// No establecer otros campos para evitar el ciclo infinito
			return person;
		}
		return new Person();
	}
}