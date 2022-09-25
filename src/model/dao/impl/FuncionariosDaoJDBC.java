package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.FuncionariosDao;
import model.entities.Departamentos;
import model.entities.Funcionarios;

public class FuncionariosDaoJDBC implements FuncionariosDao {

	private Connection conn;

	public FuncionariosDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Funcionarios obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("INSERT INTO funcionarios "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) " + "VALUES " + "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartamento().getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Funcionarios obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE funcionarios "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?");

			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartamento().getId());
			st.setInt(6, obj.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("DELETE FROM funcionarios WHERE Id = ?");

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Funcionarios findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT funcionarios.*,departamentos.Name as DepName "
					+ "FROM funcionarios INNER JOIN departamentos " + "ON funcionarios.DepartmentId = departamentos.Id "
					+ "WHERE funcionarios.Id = ?");

			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Departamentos dep = instantiateDepartment(rs);
				Funcionarios obj = instantiateSeller(rs, dep);
				return obj;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Funcionarios instantiateSeller(ResultSet rs, Departamentos dep) throws SQLException {
		Funcionarios obj = new Funcionarios();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(new java.util.Date(rs.getTimestamp("BirthDate").getTime()));
		obj.setDepartamento(dep);
		return obj;
	}

	private Departamentos instantiateDepartment(ResultSet rs) throws SQLException {
		Departamentos dep = new Departamentos();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Funcionarios> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT funcionarios.*,departamentos.Name as DepName "
					+ "FROM funcionarios INNER JOIN departamentos " + "ON funcionarios.DepartmentId = departamentos.Id "
					+ "ORDER BY Name");

			rs = st.executeQuery();

			List<Funcionarios> list = new ArrayList<>();
			Map<Integer, Departamentos> map = new HashMap<>();

			while (rs.next()) {

				Departamentos dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Funcionarios obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Funcionarios> findByDepartment(Departamentos departamentos) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement("SELECT funcionarios.*,departamentos.Name as DepName "
					+ "FROM funcionarios INNER JOIN departamentos " + "ON funcionarios.DepartmentId = departamentos.Id "
					+ "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, departamentos.getId());

			rs = st.executeQuery();

			List<Funcionarios> list = new ArrayList<>();
			Map<Integer, Departamentos> map = new HashMap<>();

			while (rs.next()) {

				Departamentos dep = map.get(rs.getInt("DepartmentId"));

				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}

				Funcionarios obj = instantiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}
}
