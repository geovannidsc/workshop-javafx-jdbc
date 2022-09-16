package model.dao;

import java.util.List;

import model.entities.Departamentos;

public interface DepartamentosDao {

	void insert(Departamentos obj);
	void update(Departamentos obj);
	void deleteById(Integer id);
	Departamentos findById(Integer id);
	List<Departamentos> findAll();
}
