package org.m7medmagdi.demo1.dao;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.m7medmagdi.demo1.model.Task;
import org.m7medmagdi.demo1.model.User;

import java.util.Date;
import java.util.List;

public class TaskDAO {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    public void save(Task task, Long userId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        try {
            // Create a proxy User object with the given userId
            User user = em.getReference(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + userId);
            }

            // Link the task to the user (only the user ID is needed)
            task.setUser(user);

            // Set the creation timestamp
            task.setCreatedAt(new Date());

            // Persist the task
            em.persist(task);

            // Commit the transaction
            em.getTransaction().commit();
        } catch (Exception e) {
            // Rollback the transaction in case of an error
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to save task: " + e.getMessage(), e);
        } finally {
            // Close the EntityManager
            em.close();
        }
    }


    public Task findById(Long taskId, Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Task t WHERE t.id = :taskId AND t.user.id = :userId", Task.class)
                    .setParameter("taskId", taskId)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // Task not found
        } finally {
            em.close();
        }
    }

    public void update(Task task, Long userId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        try {
            // Ensure the task belongs to the logged-in user
            Task existingTask = em.find(Task.class, task.getId());
            if (existingTask != null && existingTask.getUser().getId().equals(userId)) {
                em.merge(task); // Update the task
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to update task: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void delete(Long taskId, Long userId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        try {
            // Ensure the task belongs to the logged-in user
            Task task = em.find(Task.class, taskId);
            if (task != null && task.getUser().getId().equals(userId)) {
                em.remove(task); // Delete the task
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Task> findByUser(Long userId) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> tasks = em.createQuery("SELECT t FROM Task t WHERE t.user.id = :userId", Task.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1000)  // Fetch only 1000 records at a time
                    .getResultList();
            return tasks;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Failed to delete task: " + e.getMessage(), e);
        } finally {
            em.close();
        }



    }
}