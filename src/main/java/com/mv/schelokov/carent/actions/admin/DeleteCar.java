package com.mv.schelokov.carent.actions.admin;

import com.mv.schelokov.carent.actions.interfaces.AbstractAction;
import com.mv.schelokov.carent.actions.JspForward;
import com.mv.schelokov.carent.actions.exceptions.ActionException;
import com.mv.schelokov.carent.model.entity.builders.CarBuilder;
import com.mv.schelokov.carent.model.services.CarService;
import com.mv.schelokov.carent.model.services.exceptions.ServiceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Maxim Chshelokov <schelokov.mv@gmail.com>
 */
public class DeleteCar extends AbstractAction {
    private static final Logger LOG = Logger.getLogger(DeleteCar.class);
    private static final String ERROR = "Unable to delete car";
    private static final String WRONG_ID = "Wrong id parameter for car entity";

    @Override
    public JspForward execute(HttpServletRequest req, HttpServletResponse res)
            throws ActionException {
        
        JspForward forward = new JspForward();
        if (isAdmin(req)) {
            int carId = getIntParam(req, "id");
            if (carId < 1)
                throw new ActionException(WRONG_ID);
            try {
                CarService carService = new CarService();

                carService.deleteCar(new CarBuilder().setId(carId).getCar());
                
                forward.setUrl("action/car_list");
                forward.setRedirect(true);
                
                return forward;
            } catch (ServiceException ex) {
                LOG.error(ERROR, ex);
                throw new ActionException(ERROR, ex);
            }
        } else {
            sendForbidden(res);
            return forward;            
        }
    }
}
