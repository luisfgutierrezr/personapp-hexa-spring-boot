package co.edu.javeriana.as.personapp.mongo.mapper;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.mongo.document.PersonaDocument;
import co.edu.javeriana.as.personapp.mongo.document.TelefonoDocument;
import lombok.NonNull;

@Mapper
public class TelefonoMapperMongo {

	@Autowired
	private PersonaMapperMongo personaMapperMongo;

	public TelefonoDocument fromDomainToAdapter(Phone phone) {
		TelefonoDocument telefonoDocument = new TelefonoDocument();
		telefonoDocument.setId(phone.getNumber());
		telefonoDocument.setOper(phone.getCompany());
		telefonoDocument.setPrimaryDuenio(validateDuenio(phone.getOwner()));
		return telefonoDocument;
	}

	private PersonaDocument validateDuenio(@NonNull Person owner) {
		// Solo establecer la referencia por ID para evitar referencias circulares
		if (owner != null && owner.getIdentification() != null) {
			PersonaDocument personaDocument = new PersonaDocument();
			personaDocument.setId(owner.getIdentification());
			return personaDocument;
		}
		return new PersonaDocument();
	}

	public Phone fromAdapterToDomain(TelefonoDocument telefonoDocument) {
		Phone phone = new Phone();
		phone.setNumber(telefonoDocument.getId());
		phone.setCompany(telefonoDocument.getOper());
		phone.setOwner(validateOwner(telefonoDocument.getPrimaryDuenio()));
		return phone;
	}

	private @NonNull Person validateOwner(PersonaDocument duenio) {
		// Solo crear una referencia mínima con el ID para evitar referencias circulares
		// NO mapear la persona completa porque causaría un ciclo infinito
		if (duenio != null && duenio.getId() != null) {
			Person person = new Person();
			person.setIdentification(duenio.getId());
			// No establecer otros campos para evitar el ciclo infinito
			return person;
		}
		return new Person();
	}
}