package Service;

import Model.Account;
import DAO.AccountDAO;

public class AccountService {
    AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account getAccountById(int id) {
        return accountDAO.getAccountById(id);
    }

    public Account addAccount(Account account) {
        return accountDAO.insertAccount(account);
    }

    public Account loginAccount(Account account) {
        return accountDAO.loginAccount(account);
    }
}
