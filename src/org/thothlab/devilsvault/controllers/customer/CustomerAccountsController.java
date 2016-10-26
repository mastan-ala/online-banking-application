package org.thothlab.devilsvault.controllers.customer;

import java.sql.SQLException;
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
import org.thothlab.devilsvault.dao.request.ExternalRequestDaoImpl;
import org.thothlab.devilsvault.dao.request.InternalRequestDaoImpl;
import org.thothlab.devilsvault.db.model.BankAccountExternal;
import org.thothlab.devilsvault.db.model.Customer;
import org.thothlab.devilsvault.db.model.Request;
import org.thothlab.devilsvault.db.model.Transaction;

@Controller
public class CustomerAccountsController {
	
	String role;
	int userID;
	String username;
	
	public void setGlobals(HttpServletRequest request){
		role = (String) request.getSession().getAttribute("role");
		userID = (int) request.getSession().getAttribute("userID");
		username = (String) request.getSession().getAttribute("username");
		
	}
	
	@RequestMapping("/customer/SavingsAccount")
	public ModelAndView SavingAccount(HttpServletRequest request,@RequestParam("savingsPicker") String interval)
	{
		setGlobals(request);
		BankAccountExternal savingAccount = new BankAccountExternal();
		savingAccount.setExternal_users_id(userID);
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		savingAccount = CustomerDAO.getAccount(customer, savingAccount,"SAVINGS");
		CustomerAccountsDAO sAccountDAO = ctx.getBean("CustomerAccountsDAO",CustomerAccountsDAO.class);
		List<Transaction> TransactionLines = new ArrayList<Transaction>();
		if (interval.equals("Last month")) 
		{
			TransactionLines = sAccountDAO.getTransactionLines(savingAccount.getAccount_number(), 1);
		}else if (interval.equals("Last 3 months")) 
		{
			TransactionLines = sAccountDAO.getTransactionLines(savingAccount.getAccount_number(), 3);
		}else if (interval.equals("Last 6 months")) 
		{
			TransactionLines = sAccountDAO.getTransactionLines(savingAccount.getAccount_number(), 6);
		}else
		{
			TransactionLines = sAccountDAO.getTransactionLines(savingAccount.getAccount_number(), 1);
		}
		ctx.close();
		ModelAndView model = new ModelAndView("customerPages/accountsSavingsPage");
		model.addObject("customer",customer);
		model.addObject("savingAccount", savingAccount );
		model.addObject("TransactionLines",TransactionLines);
		return model;
	}
	
	@RequestMapping("/customer/CheckingAccount")
	public ModelAndView CheckingAccount(HttpServletRequest request,@RequestParam("checkingPicker") String interval){
		setGlobals(request); 
		BankAccountExternal checkingAccount = new BankAccountExternal();
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		checkingAccount = CustomerDAO.getAccount(customer, checkingAccount,"CHECKING");
		CustomerAccountsDAO sAccountDAO = ctx.getBean("CustomerAccountsDAO",CustomerAccountsDAO.class);
		List<Transaction> TransactionLines = new ArrayList<Transaction>();
		if (interval.equals("Last month")) 
		{
			TransactionLines = sAccountDAO.getTransactionLines(checkingAccount.getAccount_number(), 1);
		}else if (interval.equals("Last 3 months")) 
		{
			TransactionLines = sAccountDAO.getTransactionLines(checkingAccount.getAccount_number(), 3);
		}else if (interval.equals("Last 6 months")) 
		{
			TransactionLines = sAccountDAO.getTransactionLines(checkingAccount.getAccount_number(), 6);
		}else
		{
			TransactionLines = sAccountDAO.getTransactionLines(checkingAccount.getAccount_number(), 1);
		}
		ctx.close();
		
		ModelAndView model = new ModelAndView("customerPages/accountsCheckingsPage");
		model.addObject("Customer",customer);
		model.addObject("cAccount", checkingAccount );
		model.addObject("TransactionLines",TransactionLines);
		return model;
	}
	
	@RequestMapping(value ="/customer/addMoney")
	public ModelAndView addMoney(HttpServletRequest request,@RequestParam("amount") double amount
	,@RequestParam("accountType") String accountType)throws SQLException
	{
		setGlobals(request);
		BankAccountExternal account = new BankAccountExternal();
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		account = CustomerDAO.getAccount(customer, account,accountType);
		String modelName = "";
		String errorMessage = "";
		if(amount <= 1000d)
		{
			CustomerAccountsDAO sAccountDAO = ctx.getBean("CustomerAccountsDAO",CustomerAccountsDAO.class);
			boolean transactonstatus;
			Transaction transaction = new Transaction();
			transactonstatus = sAccountDAO.addMoney(customer,account,transaction, amount, accountType);
			if(transactonstatus == true)
			{
				modelName = "customerPages/accountssuccesspage";
				errorMessage = "money added successfully";
			}
			else
			{
				errorMessage = "Operation failed";
				modelName = "customerPages/accountserrorpage";
			}
				
		}
		else
		{
			errorMessage = "amount exceeded limit";
			modelName = "customerPages/accountserrorpage";
		}
		ctx.close();
		ModelAndView model = new ModelAndView(modelName);
		model.addObject("customer",customer);
		model.addObject("account", account );
		model.addObject("errorMessage", errorMessage );
		return model;
	}
	@RequestMapping(value ="/customer/withdrawMoney")
	public ModelAndView withdrawMoney(HttpServletRequest request,@RequestParam("amount") double amount
	,@RequestParam("accountType") String accountType) throws SQLException
	{
		setGlobals(request);
		BankAccountExternal account = new BankAccountExternal();
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		account = CustomerDAO.getAccount(customer, account,accountType);
		System.out.println("account balance "+account.getBalance());
		String modelName = "";
		String errorMessage = "";
		System.out.println("account type"+accountType);
		if(amount < account.getBalance())
		{
			CustomerAccountsDAO sAccountDAO = ctx.getBean("CustomerAccountsDAO",CustomerAccountsDAO.class);
			boolean transactonstatus;
			Transaction transaction = new Transaction();
			transactonstatus = sAccountDAO.withdrawMoney(customer,account,transaction, amount, accountType);
			if(transactonstatus == true)
			{
				modelName = "customerPages/accountssuccesspage";
				errorMessage = "money withdrawn successfully";
			}
			else
			{
				errorMessage = "Operation failed";
				modelName = "customerPages/accountserrorpage";
			}
				
		}
		else
		{
			errorMessage = "No sufficient balance";
			modelName = "customerPages/accountserrorpage";
		}
		ctx.close();
		ModelAndView model = new ModelAndView(modelName);
		model.addObject("customer",customer);
		model.addObject("savingAccount", account );
		model.addObject("errorMessage", errorMessage );
		return model;
	}
	
	@RequestMapping("/customer/accountsBalance")
	public ModelAndView helloworld(HttpServletRequest request){
		setGlobals(request);
		BankAccountExternal checkingAccount = new BankAccountExternal();
		checkingAccount.setExternal_users_id(userID);
		BankAccountExternal savingsAccount = new BankAccountExternal();
		savingsAccount.setExternal_users_id(userID);
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		checkingAccount = CustomerDAO.getAccount(customer, checkingAccount, "CHECKING");
		savingsAccount = CustomerDAO.getAccount(customer, savingsAccount, "SAVINGS");
		System.out.println("SA Bal:"+savingsAccount.getBalance());
		ctx.close();
		ModelAndView model = new ModelAndView("customerPages/accountsAddWithdrawBalance");
		model.addObject("checkingAccount", checkingAccount );
		model.addObject("savingsAccount", savingsAccount );
		return model;
	}
	
	@RequestMapping(value ="/customer/add_Withdraw_Money_home")
	public ModelAndView addMoneyHome(HttpServletRequest request)
	{
		setGlobals(request);
		BankAccountExternal savingAccount = new BankAccountExternal();
		BankAccountExternal checkingAccount = new BankAccountExternal();
		Customer customer = new Customer();
		customer.setId(userID);
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExtUserDaoImpl CustomerDAO = ctx.getBean("ExtUserDaoImpl", ExtUserDaoImpl.class);
		savingAccount = CustomerDAO.getAccount(customer, savingAccount,"SAVINGS");
		checkingAccount = CustomerDAO.getAccount(customer, savingAccount,"CHECKING");
		ctx.close();
		ModelAndView model = new ModelAndView("customerPages/accountsAddWithdrawBalance");
		model.addObject("customer",customer);
		model.addObject("savingAccount", savingAccount );
		model.addObject("checkingAccount", checkingAccount );
		return model;
	}
	
	@RequestMapping(value="/customer/processrequestexternal", method = RequestMethod.POST)
    public ModelAndView PendingRequestRejectContoller(HttpServletRequest request,RedirectAttributes redir, @RequestParam("requestID") String requestID, @RequestParam("action") String action){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        setGlobals(request);
        ModelAndView model = new ModelAndView();
        String msg = "";
        if(action.equals("approve")) {
            ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
            externalRequestDao.approveRequest(Integer.parseInt(requestID), "external", userID);    
            msg = "Request Approved!";
        }
        else {
            ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
            externalRequestDao.rejectRequest(Integer.parseInt(requestID), "external", userID);
            msg="Request Rejected!";
        } 
       ctx.close();
       model.setViewName("redirect:/customer/pendingrequest");
       redir.addFlashAttribute("error_msg",msg);
       return model;
    }
    
    @RequestMapping("/customer/pendingrequest")
    public ModelAndView PendingRequestContoller(HttpServletRequest request){
        setGlobals(request);
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
       ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
       ModelAndView model = new ModelAndView("customerPages/PendingRequest");
       List<Request> external_list =externalRequestDao.getAllRequestToApprove(userID);
       if(external_list.size() < 1)
       {
           external_list = new ArrayList<Request>();
       }
       model.addObject("external_list",external_list);
       ctx.close();
       return model;
    }
    
    @RequestMapping(value="/customer/addrequest", method = RequestMethod.POST)
    public ModelAndView modifyDetails(RedirectAttributes redir,@RequestParam("requestType") String requestType, HttpServletRequest request, @RequestParam("newValue") String newValue) {
         ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
         CustomerDAO customerDAO = ctx.getBean("customerDAO", CustomerDAO.class);
         InternalRequestDaoImpl internalrequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
         Customer customer = customerDAO.getCustomer(this.userID);
         setGlobals(request);
         internalrequestDao.raiseInternalRequest(customer, requestType, newValue,this.userID);
          ModelAndView model = new ModelAndView("redirect:/customer/userdetails");
          redir.addFlashAttribute("error_msg","Request Raised !!");
          ctx.close();
          return model;
         }
}