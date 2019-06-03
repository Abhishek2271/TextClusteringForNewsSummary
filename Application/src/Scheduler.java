import ClusterMain.ClusterCore;
import Utils.IniParser;
import com.sun.org.apache.bcel.internal.generic.NEW;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler
{
    public static void main(String args[])
    {
        RunScheduledTask();
    }


    /// <summary>
    /// Create a task that will run in the defined interval. This will collect as well as cluster data within the specified time.
    /// </summary>
    public static void RunScheduledTask()
    {
        try {
            IniParser parser = new IniParser();
            parser.CollectionDelay();

            int News_Collection_delay = parser.News_Collection_delay;
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleAtFixedRate(new Runnable()
            {
                @Override
                public void run()
                {
                    //Collect data from news sources.
                    Main.BeginProc();
                    //Cluster the collected data.
                    ClusterCore.BeginClusterProc();
                }
            }, 0, News_Collection_delay, TimeUnit.HOURS);

        } catch (Exception exp) {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }
}
