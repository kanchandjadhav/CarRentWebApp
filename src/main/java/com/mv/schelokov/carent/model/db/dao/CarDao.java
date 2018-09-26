package com.mv.schelokov.carent.model.db.dao;

import com.mv.schelokov.carent.model.db.dao.factories.EntityFromResultSetFactory;
import com.mv.schelokov.carent.model.db.dao.interfaces.AbstractSqlDao;
import com.mv.schelokov.carent.model.db.dao.interfaces.Criteria;
import com.mv.schelokov.carent.model.db.dao.interfaces.SqlCriteria;
import com.mv.schelokov.carent.model.entity.Car;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class CarDao extends AbstractSqlDao<Car> {
    
    public interface ReadCriteria extends SqlCriteria {}
    
    public interface DeleteCriteria extends SqlCriteria {}
    
    public static final Criteria SELECT_ALL_CRITERIA = new SelectAllCriteria();
    public static final Criteria SELECT_AVAILABLE_CRITERIA = 
            new SelectAvailableCriteria();
    
    public static class SelectAllCriteria implements ReadCriteria {

        private static final String QUERY = "SELECT car_id,license_plate,"
                + "year_of_make,price,model_id,model_name,make_id,make_name,"
                + "available FROM cars_full";

        @Override
        public String toSqlQuery() {
            return QUERY;
        }

        @Override
        public void setStatement(PreparedStatement ps) throws SQLException {}
    }
    
    public static class SelectAvailableCriteria extends SelectAllCriteria {
        private static final String QUERY = " WHERE available=b'1'";
        @Override
        public String toSqlQuery() {
            return super.toSqlQuery().concat(QUERY);
        }
    }
    
    public static class FindByIdCriteria extends SelectAllCriteria {

        private static final String QUERY = " WHERE car_id=?";
        private static final int CAR_ID = 1;

        private final int id;

        public FindByIdCriteria(int id) {
            this.id = id;
        }

        @Override
        public String toSqlQuery() {
            return super.toSqlQuery().concat(QUERY);
        }

        @Override
        public void setStatement(PreparedStatement ps) throws SQLException {
            ps.setInt(CAR_ID, id);
        }
    }
    
    private static final String CREATE_QUERY = "INSERT INTO cars (model_id,"
            + "license_plate,year_of_make,price,available) VALUES (?,?,?,?,?)";
    private static final String REMOVE_QUERY = "DELETE FROM cars WHERE"
            + " car_id=?";
    private static final String UPDATE_QUERY = "UPDATE cars SET model_id=?,"
            + "license_plate=?,year_of_make=?,price=?,available=? "
            + "WHERE car_id=?";
    
    /**
     * The Field enum has column names for read methods and number of column for
     * the update method and the add method (in the NUMBER attribute)
     */
    enum Fields {
        CAR_ID(6), LICENSE_PLATE(2), YEAR_OF_MAKE(3), PRICE(4), MODEL(1), NAME, 
        MAKE, MAKE_NAME, AVAILABLE(5);
        
        int NUMBER;
        
        Fields(int number) {
            this.NUMBER = number;
        }
        Fields() {
        }
    }
    
    public CarDao(Connection connection) {
        super(connection);
    }

        @Override
    protected String getCreateQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected String getRemoveQuery() {
        return REMOVE_QUERY;
    }

    @Override
    protected String getUpdateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected Car createItem(ResultSet rs) throws SQLException {
        return new EntityFromResultSetFactory(rs).createCar();
    }

    @Override
    protected void setStatement(PreparedStatement ps, Car item, 
            boolean isUpdateStatement) throws SQLException {
        ps.setInt(Fields.MODEL.NUMBER, item.getCarModel().getId());
        ps.setString(Fields.LICENSE_PLATE.NUMBER, item.getLicensePlate());
        ps.setInt(Fields.YEAR_OF_MAKE.NUMBER, item.getYearOfMake());
        ps.setInt(Fields.PRICE.NUMBER, item.getPrice());
        ps.setBoolean(Fields.AVAILABLE.NUMBER, item.isAvailable());
        if (isUpdateStatement)
            ps.setInt(Fields.CAR_ID.NUMBER, item.getId());
    }

    @Override
    protected boolean isReadCriteriaInstance(Criteria criteria) {
        return criteria instanceof ReadCriteria;
    }
    
    @Override
    protected boolean isDeleteCriteriaInstance(Criteria criteria) {
        return criteria instanceof DeleteCriteria;
    }
}
