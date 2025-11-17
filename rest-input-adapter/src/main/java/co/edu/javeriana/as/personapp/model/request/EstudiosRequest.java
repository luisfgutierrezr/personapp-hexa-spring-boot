package co.edu.javeriana.as.personapp.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudiosRequest {
	private String personId;
	private String professionId;
	private String graduationDate; // Formato: YYYY-MM-DD
	private String universityName;
	private String database;
}

