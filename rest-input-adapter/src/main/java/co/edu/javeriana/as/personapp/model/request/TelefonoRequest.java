package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoRequest {
	private String number;
	private String company;
	private Integer ownerId; // ID de la persona dueña del teléfono
	private String database;
}

