package platanus.jasperserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Servlet implementation class RenderServlet
 */
public class RenderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public RenderServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Get a JDBC Class
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}
		
		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;
		
		// Connect to DB
		try { 
			System.out.println("jdbc:mysql://" + request.getParameter("db_host") + ":3306/" + request.getParameter("db_name"));
			connection = DriverManager
				.getConnection(
					"jdbc:mysql://" + request.getParameter("db_host") + ":3306/" + request.getParameter("db_name"),
					request.getParameter("db_user"),
					request.getParameter("db_password")
				);
	 
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}
		
		// Generate report parameter hash
		HashMap jasperParams = new HashMap();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			// ignore request meta parameters (sure about this?)
			if(paramName == "db_host" || paramName == "db_user" || 
					paramName == "db_password" || paramName == "source") continue;
			jasperParams.put(paramName, request.getParameter(paramName));
		}
		
		try {
			// Load and fill report
			InputStream input = new URL(request.getParameter("source")).openStream();
			JasperPrint jasperprint = JasperFillManager.fillReport(input, jasperParams, connection);
		
			// Export report to pdf and stream back to browser
			byte[] pdfasbytes = JasperExportManager.exportReportToPdf(jasperprint);
			ServletOutputStream outstream = response.getOutputStream();
			response.setContentType("application/pdf");
			response.setContentLength(pdfasbytes.length);
			response.setHeader("Content-disposition", "inline; filename=\"Report.pdf\"");
			outstream.write(pdfasbytes);
			
		} catch(JRException e) {
			System.out.println("Report generation failed");
			e.printStackTrace();
		}
	}
}
