package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartamentosDao;
import model.entities.Departamentos;

public class DepartamentosService {
	
	
	private DepartamentosDao dao = DaoFactory.createDepartmentDao();
	
	public List<Departamentos>  findAll(){
	
	
		
		
		return dao.findAll();
		
	}
	
	

}
