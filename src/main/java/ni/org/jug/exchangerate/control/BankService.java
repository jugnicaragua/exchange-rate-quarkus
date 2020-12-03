package ni.org.jug.exchangerate.control;

import ni.org.jug.exchangerate.boundary.CookieRequest;
import ni.org.jug.exchangerate.boundary.InvalidInputException;
import ni.org.jug.exchangerate.entity.Bank;
import ni.org.jug.exchangerate.entity.Cookie;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

/**
 * @author aalaniz
 */
@ApplicationScoped
@Transactional
public class BankService {

    public void addCookie(Integer bankId, Cookie cookie) {
        Objects.requireNonNull(bankId);
        Objects.requireNonNull(cookie);
        if (cookie.getId() != null) {
            throw InvalidInputException.requireNullId(Cookie.ENTITY_NAME);
        }

        Bank bank = Bank.findById(bankId);
        bank.addCookie(cookie);
    }

    public void updateCookie(Integer bankId, CookieRequest cookieRequest) {
        Objects.requireNonNull(bankId);
        Objects.requireNonNull(cookieRequest);
        if (cookieRequest.id == null) {
            throw InvalidInputException.requireNonNullId(Cookie.ENTITY_NAME);
        }

        Bank bank = Bank.findById(bankId);
        Cookie managedCookie = bank.findCookieById(cookieRequest.id);

        managedCookie.setName(cookieRequest.name);
        managedCookie.setValue(cookieRequest.value);
    }

    public void removeCookie(Integer bankId, Integer cookieId) {
        Objects.requireNonNull(bankId);
        Objects.requireNonNull(cookieId);

        Bank bank = Bank.findById(bankId);
        Cookie cookie = bank.findCookieById(cookieId);
        bank.removeCookie(cookie);
    }
}
