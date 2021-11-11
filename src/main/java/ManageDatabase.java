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
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }

        ManageDatabase MD = new ManageDatabase();

        Schedule schedule = MD.addSchedule(986787, "22 October Friday: 09:00-10:30 Physics 10:40-12:10 Physics");
        Group group = MD.addGroup("MEH-482201", 986787);    // SECOND ARGUMENT SHOULD BE A CLASS INSTEAD
        User user = MD.addUser(1234567890, "MEH-482201");    // SECOND ARGUMENT SHOULD BE A CLASS INSTEAD

        MD.listDatabase();
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

    public Group addGroup(String id, int groupID)
    {
        Session session = factory.openSession();
        Transaction tx = null;
        Group group = null;

        try {
            tx = session.beginTransaction();
            group = new Group(id, groupID);
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

    public User addUser(long id, String groupID)
    {
        Session session = factory.openSession();
        Transaction tx = null;
        User user = null;

        try {
            tx = session.beginTransaction();
            user = new User(id, groupID);
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
            List users = session.createQuery("FROM USERS").list();
            for (Iterator iterator = users.iterator(); iterator.hasNext();)
            {
                User user = (User) iterator.next();
                output = output += ("Chat ID: " + user.getId());
                output = output += ("Group: " + user.getGroupID());
            }
            tx.commit();

            tx = session.beginTransaction();
            List groups = session.createQuery("FROM GROUPS").list();
            for (Iterator iterator = groups.iterator(); iterator.hasNext();)
            {
                Group group = (Group) iterator.next();
                output = output += ("Group: " + group.getId());
                output = output += ("Schedule ID: " + group.getScheduleID());
            }
            tx.commit();

            tx = session.beginTransaction();
            List schedules = session.createQuery("FROM SCHEDULES").list();
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

