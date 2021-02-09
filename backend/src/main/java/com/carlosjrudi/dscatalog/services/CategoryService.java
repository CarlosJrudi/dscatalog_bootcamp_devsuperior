package com.carlosjrudi.dscatalog.services;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.carlosjrudi.dscatalog.DTO.CategoryDTO;
import com.carlosjrudi.dscatalog.entities.Category;
import com.carlosjrudi.dscatalog.repositories.CategoryRepository;
import com.carlosjrudi.dscatalog.services.exceptions.DataBaseException;
import com.carlosjrudi.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional (readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest){
		Page <Category> list = repository.findAll(pageRequest);		
		return list.map(x -> new CategoryDTO(x));
	}
	
	@Transactional (readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found / Objeto não encontrado."));	
		return new CategoryDTO(entity);
	}
	
	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
		
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity = repository.getOne(id);
			entity .setName(dto.getName());
			entity =  repository.save(entity);
			return new CategoryDTO (entity);
		}
		catch (EntityNotFoundException e){
			throw new ResourceNotFoundException("Id não encontrado / Id not found: " + id);
		}		
	}

	public void delete(Long id) {
		
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado / Id not found: " + id);
		}
		catch (DataIntegrityViolationException e){
			throw new DataBaseException("Violação de integridade do banco / Database integrity voilation.");
		}
		
	}
}
