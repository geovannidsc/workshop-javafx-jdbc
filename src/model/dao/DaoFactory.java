package model.dao;

import db.DB;
import model.dao.impl.DepartamentosDaoJDBC;
import model.dao.impl.FuncionariosDaoJDBC;

public class DaoFactory {

	public static FuncionariosDao createSellerDao() {
		return new FuncionariosDaoJDBC(DB.getConnection());
	}
	
	public static DepartamentosDao createDepartmentDao() {
		return new DepartamentosDaoJDBC(DB.getConnection());
	}
}
