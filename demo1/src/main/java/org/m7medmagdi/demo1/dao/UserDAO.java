package org.m7medmagdi.demo1.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.m7medmagdi.demo1.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {
    public static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public void save(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public User findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // No user found with the given email
        } catch (Exception e) {
            throw new RuntimeException("Failed to find user by email: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public boolean validatePassword(String email, String password) {
        try {
            User user = findByEmail(email);
            if (user != null) {
                return BCrypt.checkpw(password, user.getPassword());
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate password: " + e.getMessage(), e);
        }
    }
}
