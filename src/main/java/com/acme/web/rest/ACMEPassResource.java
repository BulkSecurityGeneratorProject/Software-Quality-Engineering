package com.acme.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.acme.service.ACMEPassService;
import com.acme.service.dto.ACMEPassDTO;
import com.acme.web.rest.util.HeaderUtil;
import com.acme.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ACMEPass.
 */
@RestController
@RequestMapping("/api")
public class ACMEPassResource {

	private final Logger log = LoggerFactory.getLogger(ACMEPassResource.class);

	@Inject
	private ACMEPassService acmePassService;

	/**
	 * POST /acme-passes : Create a new acmePass.
	 *
	 * @param acmePass the acmePass to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new acmePass, or with status 400 (Bad
	 * Request) if the acmePass has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PostMapping("/acme-passes")
	@Timed
	public ResponseEntity<ACMEPassDTO> createACMEPass(@Valid @RequestBody ACMEPassDTO acmePass) throws URISyntaxException {
		log.debug("REST request to save ACMEPass : {}", acmePass);
		if (acmePass.getId() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("acmePass", "idexists", "A new acmePass cannot already have an ID")).body(null);
		}
		ACMEPassDTO result = acmePassService.save(acmePass);
		return ResponseEntity.created(new URI("/api/acme-passes/" + result.getId()))
			.headers(HeaderUtil.createEntityCreationAlert("acmePass", result.getId().toString()))
			.body(result);
	}

	/**
	 * PUT /acme-passes : Updates an existing acmePass.
	 *
	 * @param acmePass the acmePass to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated acmePass, or with status 400 (Bad
	 * Request) if the acmePass is not valid, or with status 500 (Internal Server Error) if the acmePass couldnt be
	 * updated
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@PutMapping("/acme-passes")
	@Timed
	public ResponseEntity<ACMEPassDTO> updateACMEPass(@Valid @RequestBody ACMEPassDTO acmePass) throws URISyntaxException {
		log.debug("REST request to update ACMEPass : {}", acmePass);
		if (acmePass.getId() == null) {
			return createACMEPass(acmePass);
		}
		ACMEPassDTO result = acmePassService.save(acmePass);
		return ResponseEntity.ok()
			.headers(HeaderUtil.createEntityUpdateAlert("acmePass", acmePass.getId().toString()))
			.body(result);
	}

	/**
	 * GET /acme-passes : get all the acmePasses.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of acmePasses in body
	 * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
	 */
	@GetMapping("/acme-passes")
	@Timed
	public ResponseEntity<List<ACMEPassDTO>> getAllACMEPasses(@ApiParam Pageable pageable)
		throws URISyntaxException {
		log.debug("REST request to get a page of ACMEPasses");
		Page<ACMEPassDTO> page = acmePassService.findAllOfCurrentUser(pageable);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/acme-passes");
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /acme-passes/:id : get the "id" acmePass.
	 *
	 * @param id the id of the acmePass to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the acmePass, or with status 404 (Not Found)
	 */
	@GetMapping("/acme-passes/{id}")
	@Timed
	public ResponseEntity<ACMEPassDTO> getACMEPass(@PathVariable Long id) {
		log.debug("REST request to get ACMEPass : {}", id);
		ACMEPassDTO acmePass = acmePassService.findOne(id);
		return Optional.ofNullable(acmePass)
			.map(result -> new ResponseEntity<>(
					result,
					HttpStatus.OK))
			.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /acme-passes/:id : delete the "id" acmePass.
	 *
	 * @param id the id of the acmePass to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@DeleteMapping("/acme-passes/{id}")
	@Timed
	public ResponseEntity<Void> deleteACMEPass(@PathVariable Long id) {
		log.debug("REST request to delete ACMEPass : {}", id);
		acmePassService.delete(id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("acmePass", id.toString())).build();
	}

}
