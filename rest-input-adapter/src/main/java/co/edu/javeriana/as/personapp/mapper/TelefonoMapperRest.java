package co.edu.javeriana.as.personapp.mapper;

import co.edu.javeriana.as.personapp.common.annotations.Mapper;
import co.edu.javeriana.as.personapp.domain.Person;
import co.edu.javeriana.as.personapp.domain.Phone;
import co.edu.javeriana.as.personapp.model.request.TelefonoRequest;
import co.edu.javeriana.as.personapp.model.response.TelefonoResponse;

@Mapper
public class TelefonoMapperRest {
	
	public TelefonoResponse fromDomainToAdapterRestMaria(Phone phone) {
		return fromDomainToAdapterRest(phone, "MariaDB");
	}
	
	public TelefonoResponse fromDomainToAdapterRestMongo(Phone phone) {
		return fromDomainToAdapterRest(phone, "MongoDB");
	}
	
	public TelefonoResponse fromDomainToAdapterRest(Phone phone, String database) {
		Integer ownerId = phone.getOwner() != null ? phone.getOwner().getIdentification() : null;
		return new TelefonoResponse(
				phone.getNumber(), 
				phone.getCompany(), 
				ownerId,
				database,
				"OK");
	}

	public Phone fromAdapterToDomain(TelefonoRequest request, Person owner) {
		if (request == null) {
			return null;
		}
		
		if (request.getNumber() == null || request.getNumber().isEmpty()) {
			throw new IllegalArgumentException("El número de teléfono no puede estar vacío");
		}
		
		if (request.getCompany() == null || request.getCompany().isEmpty()) {
			throw new IllegalArgumentException("La compañía no puede estar vacía");
		}
		
		if (owner == null) {
			throw new IllegalArgumentException("El dueño del teléfono es requerido");
		}
		
		return new Phone(
			request.getNumber(),
			request.getCompany(),
			owner
		);
	}
}

