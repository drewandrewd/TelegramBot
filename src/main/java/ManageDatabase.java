import java.util.List;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ManageDatabase {
    public static SessionFactory factory;
    public static void fillDatabase()
    {
        System.out.println("FILL DATABASE ENTERED");

        ManageDatabase MD = new ManageDatabase();
        System.out.println("NEW MANAGEDATABASE OK");

        Schedule schedule = MD.addSchedule(986787, "22 October Friday: 09:00-10:30 Physics 10:40-12:10 Physics");
        System.out.println("NEW SCHEDULE OK");
        StudentGroup group = MD.addGroup("MEH-482201", schedule);    // SECOND ARGUMENT SHOULD BE A CLASS INSTEAD
        System.out.println("NEW GROUP OK");
        User user = MD.addUser(1234567890, group);    // SECOND ARGUMENT SHOULD BE A CLASS INSTEAD
        System.out.println("NEW USER OK");

        //MD.listDatabase();
    }

    public Schedule addSchedule(int id, String text)
    {
        Session session = factory.openSession();
        Transaction tx = null;
        Schedule schedule = null;

        try {
            tx = session.beginTransaction();
            schedule = new Schedule(id, text);
            session.save(schedule);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return schedule;
    }

    public StudentGroup addGroup(String id, Schedule schedule)
    {
        Session session = factory.openSession();
        Transaction tx = null;
        StudentGroup group = null;

        try {
            tx = session.beginTransaction();
            group = new StudentGroup(id, schedule);
            session.save(group);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return group;
    }

    public User addUser(long id, StudentGroup group)
    {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;

        try {
            tx = session.beginTransaction();
            user = new User(id, group);
            session.save(user);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return user;
    }

    public static String listDatabase()
    {
        String output = "";
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List users = session.createQuery("FROM User").list();       // not FROM USERS: https://stackoverflow.com/a/47624917
            for (Iterator iterator = users.iterator(); iterator.hasNext();)
            {
                User user = (User) iterator.next();
                output = output += ("Chat ID: " + user.getId());
                output = output += ("Group: " + user.getStudentGroup().getId());
            }
            tx.commit();

            tx = session.beginTransaction();
            List groups = session.createQuery("FROM StudentGroup").list();
            for (Iterator iterator = groups.iterator(); iterator.hasNext();)
            {
                StudentGroup group = (StudentGroup) iterator.next();
                output = output += ("Group: " + group.getId());
                output = output += ("Schedule ID: " + group.getSchedule().getId());
            }
            tx.commit();

            tx = session.beginTransaction();
            List schedules = session.createQuery("FROM Schedule").list();
            for (Iterator iterator = schedules.iterator(); iterator.hasNext();)
            {
                Schedule schedule = (Schedule) iterator.next();
                output = output += ("Schedule ID: " + schedule.getId());
                output = output += ("Text: " + schedule.getDayJSON());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        return output;
    }
}

