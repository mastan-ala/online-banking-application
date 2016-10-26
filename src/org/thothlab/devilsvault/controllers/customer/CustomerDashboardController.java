package org.thothlab.devilsvault.controllers.customer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thothlab.devilsvault.dao.customer.CustomerAccountsDAO;
import org.thothlab.devilsvault.dao.customer.CustomerDAO;
import org.thothlab.devilsvault.dao.customer.ExtUserDaoImpl;
import org.thothlab.devilsvault.dao.userauthentication.UserAuthenticationDaoImpl;
import org.thothlab.devilsvault.db.model.BankAccountExternal;
import org.thothlab.devilsvault.db.model.Customer;
import org.thothlab.devilsvault.db.model.Transaction;

@Controller
public class CustomerDashboardController {
	
	String role;
	int userID;
	String username;
	
	public void setGlobals(HttpServletRequest request){
		role = (String) request.getSession().getAttribute("role");
		userID = (int) request.getSession().getAttribute("userID");
		username = (String) request.getSession().getAttribute("username");
		
	}
	
	@RequestMapping("/customer/home")
	public ModelAndView customerHome(HttpServletRequest request){
		
		setGlobals(request);
		BankAccountExternal checkingAccount = new BankAccountExternal();
		checkingAccount.setExternal_users_id(userID);
		BankAccountExternal savingAccount = new BankAccountExternal();
		savingAccount.setExternal_users_id(userID);
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		checkingAccount = CustomerDAO.getAccount(customer, checkingAccount, "CHECKING");
		savingAccount = CustomerDAO.getAccount(customer, savingAccount, "SAVINGS");
		Double SavingsAccBal = (double)savingAccount.getBalance();
		Double CheckingAcctBal = (double)checkingAccount.getBalance();
		CustomerAccountsDAO sAccountDAO = ctx.getBean("CustomerAccountsDAO",CustomerAccountsDAO.class);
		List<Transaction> TransactionLines_checking = new ArrayList<Transaction>();
		List<Transaction> TransactionLines_savings = new ArrayList<Transaction>();
		List<Transaction> TransactionLines_credit = new ArrayList<Transaction>();
		TransactionLines_checking = sAccountDAO.getTransactionLines(checkingAccount.getAccount_number(), 1);
		if(TransactionLines_checking.size() >5)
		TransactionLines_checking=TransactionLines_checking.subList(0, 5);
		TransactionLines_savings = sAccountDAO.getTransactionLines(savingAccount.getAccount_number(), 1);
		if(TransactionLines_savings.size() > 5)
		TransactionLines_savings=TransactionLines_savings.subList(0, 5);

		//TransactionLines_credit = sAccountDAO.getTransactionLines(101, 1);
		ctx.close();
		ModelAndView model = new ModelAndView("customerPages/customerDashboard");
		model.addObject("Customer",customer);
		model.addObject("checkingAccount", checkingAccount );
		model.addObject("savingsAccount", savingAccount );
		model.addObject("SavingsAccBal",SavingsAccBal);
		model.addObject("CheckingAccBal", CheckingAcctBal);
		model.addObject("TransactionLinesCH", TransactionLines_checking);
		model.addObject("TransactionLinesSV", TransactionLines_savings);
		model.addObject("TransactionLinesCC", TransactionLines_credit);
		return model;
	}
	
	@RequestMapping("/customer/userdetails")
	public ModelAndView UserDetailsContoller(HttpServletRequest request){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		CustomerDAO externalDao = ctx.getBean("customerDAO", CustomerDAO.class);
		setGlobals(request);
		Customer customer = externalDao.getCustomer(userID);
		ModelAndView model = new ModelAndView("customerPages/customerUserDetails");
		model.addObject("user",customer);
		ctx.close();
		return model;
	}
	
	@RequestMapping(value="/customer/changepassword", method = RequestMethod.POST)
    public ModelAndView changePasswordInternal(RedirectAttributes redir, @RequestParam("oldpassword") String oldPassword, HttpServletRequest request, @RequestParam("newpassword") String newPassword,@RequestParam("confirmpassword") String confirmPassword) {
		ModelAndView model = new ModelAndView();
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		setGlobals(request);
		UserAuthenticationDaoImpl userauthenticationDao = ctx.getBean("userAuthenticationDao", UserAuthenticationDaoImpl.class);
		String message = userauthenticationDao.changePassword(oldPassword, newPassword, confirmPassword, userID,role);
		ctx.close();
		model.setViewName("redirect:/customer/userdetails");
        redir.addFlashAttribute("message",message);
        return model;
		

    }
	
	@RequestMapping(value="/customer/transferotp")
    public ModelAndView transfer(HttpServletRequest request) {
		ModelAndView model = new ModelAndView("customerPages/transferOTPPage");
		return model;
		

    }
	
}