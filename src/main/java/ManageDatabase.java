import java.util.List;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManageDatabase
{
    private static final Logger logger = LoggerFactory.getLogger(ManageDatabase.class);

    public static SessionFactory factory;
    public static void fillDatabase()
    {
        logger.debug("Starting to fill the database.");

        ManageDatabase MD = new ManageDatabase();

        for (var item : Main.groupMap.entrySet())
        {
            Schedule schedule = MD.addSchedule(Integer.parseInt(item.getKey()), "Nothing\nIf this is a query response, something went wrong\n");
            StudentGroup group = MD.addGroup(item.getValue().getItem1(), schedule);
        }
    }

    private Schedule addSchedule(int id, String text)
    {
        Session session = factory.openSession();
        Transaction transaction = null;
        Schedule schedule = null;

        try
        {
            transaction = session.beginTransaction();
            schedule = new Schedule(id, text);
            session.save(schedule);
            transaction.commit();
        }
        catch (HibernateException e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error adding a schedule to database table. The exception and stack trace follows.", e);
        }
        finally
        {
            session.close();
        }
        return schedule;
    }

    private StudentGroup addGroup(String id, Schedule schedule)
    {
        Session session = factory.openSession();
        Transaction transaction = null;
        StudentGroup group = null;

        try
        {
            transaction = session.beginTransaction();
            group = new StudentGroup(id, schedule);
            session.save(group);
            transaction.commit();
        }
        catch (HibernateException e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error adding a studentgroup to database table. The exception and stack trace follows.", e);
        }
        finally
        {
            session.close();
        }
        return group;
    }

    private static User addUser(long id, StudentGroup group)
    {
        Session session = factory.openSession();
        Transaction transaction = null;
        User user = null;

        try
        {
            transaction = session.beginTransaction();
            user = new User(id, group);
            session.save(user);
            transaction.commit();
        }
        catch (HibernateException e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error adding a user to database table. The exception and stack trace follows.", e);
        }
        finally
        {
            session.close();
        }
        return user;
    }

    public static void updateSchedule(int ScheduleID, String newScheduleText)
    {
        Session session = factory.openSession();
        Transaction transaction = null;

        try
        {
            transaction = session.beginTransaction();
            Schedule schedule = session.get(Schedule.class, ScheduleID);
            schedule.setDayJSON(newScheduleText);
            session.update(schedule);
            transaction.commit();
        }
        catch (HibernateException e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error updating a group's schedule. The exception and stack trace follows.", e);
        }
        finally
        {
            session.close();
        }
    }

    public static Tuple<Boolean, String> tryGetOwnSchedule(long userID)     // needs more try catches?
    {
        Session session = factory.openSession();

        User user;
        user = session.get(User.class, userID);
        if (user == null)
        {
            session.close();
            return new Tuple<>(false, null);
        }

        var studentGroup = user.getStudentGroup();
        if (System.currentTimeMillis() - Main.groupMap.get(String.valueOf(studentGroup.getSchedule().getId())).getItem2() > 3600000)      // if cached info is too old
        {
            var result = DatabaseUpdater.tryGetNewScheduleForGroup(String.valueOf(studentGroup.getSchedule().getId()));
            if (result.getItem1())
                updateSchedule(studentGroup.getSchedule().getId(), result.getItem2());
            else
                return new Tuple<>(false, null);
        }

        var schedule = studentGroup.getSchedule().getDayJSON();
        session.close();
        return new Tuple<>(true, schedule);
    }

    public static Tuple<Boolean, String> tryGetScheduleByStudentGroupName(String studentGroupName)
    {
        Session session = factory.openSession();

        StudentGroup studentGroup;
        studentGroup = session.get(StudentGroup.class, studentGroupName);
        if (studentGroup == null)
        {
            session.close();
            return new Tuple<>(false, null);
        }

        if (System.currentTimeMillis() - Main.groupMap.get(String.valueOf(studentGroup.getSchedule().getId())).getItem2() > 3600000)      // if cached info is too old
        {
            System.out.println(studentGroup.getSchedule().getId());
            var result = DatabaseUpdater.tryGetNewScheduleForGroup(String.valueOf(studentGroup.getSchedule().getId()));
            if (result.getItem1())
                updateSchedule(studentGroup.getSchedule().getId(), result.getItem2());
            else
                return new Tuple<>(false, null);
        }

        var schedule = studentGroup.getSchedule().getDayJSON();
        return new Tuple<>(true, schedule);
    }

    public static boolean tryChangeStudentGroup(long userID, String newGroupName)
    {
        Session session = factory.openSession();

        StudentGroup studentGroup;
        studentGroup = session.get(StudentGroup.class, newGroupName);       // checking whether the group exists
        if (studentGroup == null)
            return false;

        Transaction transaction = null;
        try
        {
            transaction = session.beginTransaction();
            User user;
            user = session.get(User.class, userID);

            if (user == null)
                user = addUser(userID, studentGroup);

            if (user != null)
            {
                user.setStudentGroup(studentGroup);
                session.update(user);
                transaction.commit();
                return true;
            }
            else
                throw new Exception("User was somehow still null after adding them to database, thus unable to change their studentgroup.");
        }
        catch (Exception e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error changing a user's studentgroup. The exception and stack trace follows.", e);
            return false;
        }
        finally
        {
            session.close();
        }
    }

    public static String tryDeleteInfo(long userID)
    {
        Session session = factory.openSession();
        Transaction transaction = null;

        User user;
        user = session.get(User.class, userID);
        if (user == null)
        {
            session.close();
            return "Ваша информация отсутствует";
        }

        try
        {
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
            return "Успех";
        }
        catch (HibernateException e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error when deleting a user's info. The exception and stack trace follows.", e);
            return "Произошла ошибка, обратитесь к администратору сервиса";
        }
        finally
        {
            session.close();
        }
    }

    public static String listDatabase()
    {
        Session session = factory.openSession();
        Transaction transaction = null;

        StringBuilder output = new StringBuilder();

        try
        {
            transaction = session.beginTransaction();
            output.append("USERS:\n");
            var users = session.createQuery("FROM User").list();       // not FROM USERS: https://stackoverflow.com/a/47624917
            var amount = 0;         // limited due to telegram restrictions
            for (Object obj : users)
            {
                User user = (User) obj;
                output.append("Chat ID: ").append(user.getId()).append(" ");
                output.append("Group: ").append(user.getStudentGroup().getId());
                output.append("\n");
                ++amount;
                if (amount > 10)
                    break;
            }
            transaction.commit();

            transaction = session.beginTransaction();
            output.append("STUDENTGROUPS:\n");
            var studentgroups = session.createQuery("FROM StudentGroup").list();
            amount = 0;
            for (Object obj : studentgroups)
            {
                StudentGroup group = (StudentGroup) obj;
                output.append("Group: ").append(group.getId()).append(" ");
                output.append("Schedule ID: ").append(group.getSchedule().getId());
                output.append("\n");
                ++amount;
                if (amount > 10)
                    break;
            }
            transaction.commit();

            amount = 0;
            transaction = session.beginTransaction();
            output.append("SCHEDULES:\n");
            var schedules = session.createQuery("FROM Schedule").list();
            for (Object obj : schedules) {
                Schedule schedule = (Schedule) obj;
                output.append("Schedule ID: ").append(schedule.getId()).append(" ");
                output.append("Text:\n").append(schedule.getDayJSON());
                output.append("\n");
                ++amount;
                if (amount > 10)
                    break;
            }
            transaction.commit();
        }
        catch (HibernateException e)
        {
            if (transaction != null)
                transaction.rollback();
            logger.error("Error listing the database. The exception and stack trace follows.", e);
        }
        finally
        {
            session.close();
        }

        return output.toString();
    }
}

