package co.edu.javeriana.as.personapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import co.edu.javeriana.as.personapp.adapter.EstudiosInputAdapterRest;
import co.edu.javeriana.as.personapp.model.request.EstudiosRequest;
import co.edu.javeriana.as.personapp.model.response.EstudiosResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/estudios")
public class EstudiosControllerV1 {
	
	@Autowired
	private EstudiosInputAdapterRest estudiosInputAdapterRest;
	
	@ResponseBody
	@GetMapping(path = "/{database}", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<EstudiosResponse> estudios(@PathVariable String database) {
		log.info("Into estudios REST API");
		return estudiosInputAdapterRest.historial(database.toUpperCase());
	}
	
	@ResponseBody
	@GetMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public EstudiosResponse obtenerEstudiosPorId(@PathVariable String database, @PathVariable Integer personId, @PathVariable Integer professionId) {
		log.info("Into obtenerEstudiosPorId REST API");
		return estudiosInputAdapterRest.obtenerEstudiosPorId(database.toUpperCase(), personId, professionId);
	}
	
	@ResponseBody
	@PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudiosResponse crearEstudios(@RequestBody EstudiosRequest request) {
		log.info("esta en el metodo crearEstudios en el controller del api");
		return estudiosInputAdapterRest.crearEstudios(request);
	}
	
	@ResponseBody
	@PutMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public EstudiosResponse actualizarEstudios(@PathVariable String database, @PathVariable Integer personId, @PathVariable Integer professionId, @RequestBody EstudiosRequest request) {
		log.info("esta en el metodo actualizarEstudios en el controller del api");
		return estudiosInputAdapterRest.actualizarEstudios(database.toUpperCase(), personId, professionId, request);
	}
	
	@ResponseBody
	@DeleteMapping(path = "/{database}/{personId}/{professionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public EstudiosResponse eliminarEstudios(@PathVariable String database, @PathVariable Integer personId, @PathVariable Integer professionId) {
		log.info("esta en el metodo eliminarEstudios en el controller del api");
		return estudiosInputAdapterRest.eliminarEstudios(database.toUpperCase(), personId, professionId);
	}
}

