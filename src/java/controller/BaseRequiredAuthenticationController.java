package controller;

import dal.EmployeeDao;
import dal.FeatureDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Employee;
import model.User;

public abstract class BaseRequiredAuthenticationController extends HttpServlet {

    private User getAuthenticatedUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    private Employee getAuthenticatedEmployee(HttpServletRequest req) {
        User user = getAuthenticatedUser(req);
        EmployeeDao ed = new EmployeeDao();
        if (user != null && isAuthorized(req, user)) {
            Employee employee = ed.get(user);
            return employee;
        }
        return null;
    }

    public boolean isAuthorized(HttpServletRequest request, User user) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String path = requestURI.substring(contextPath.length());

        int roleId = user.getRoleid();
        FeatureDao fd = new FeatureDao();
        List<String> listFeature = fd.getFeatureList(roleId);

        for (String feature : listFeature) {
            if (path.equals(feature)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Employee employee = getAuthenticatedEmployee(req);
        if (employee != null) {
            doPost(req, resp, employee);
        } else {
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Employee employee = getAuthenticatedEmployee(req);
        if (employee != null) {
            doGet(req, resp, employee);
        } else {
            req.getRequestDispatcher("/WEB-INF/login.jsp").forward(req, resp);
        }
    }

    protected abstract void doPost(HttpServletRequest req, HttpServletResponse resp, Employee employee) throws ServletException, IOException;

    protected abstract void doGet(HttpServletRequest req, HttpServletResponse resp, Employee employee) throws ServletException, IOException;
}
