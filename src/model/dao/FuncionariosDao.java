package model.dao;

import java.util.List;

import model.entities.Departamentos;
import model.entities.Funcionarios;

public interface FuncionariosDao {

	void insert(Funcionarios obj);
	void update(Funcionarios obj);
	void deleteById(Integer id);
	Funcionarios findById(Integer id);
	List<Funcionarios> findAll();
	List<Funcionarios> findByDepartment(Departamentos department);
}
