package Utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.format.DateTimeFormatter;

/*
    <Created Date> Nov 9, 2018 </CreatedDate>
    <LastUpdated> Nov 10, 2018 </LastUpdated>
    <About>
        This class is used to validate time/date/interval related computations.
    </About>
*/
public class TimeValidations
{
    /// <summary>
    /// Checks whether the given range of dates is within 24 hour time period
    ///</summary>
    /// <param name="StartTime">Start date</param>
    /// <param name="EndTime">End date</param>
    /// <param name="TimeInterval">Interval at which the news should be considered/ collected (in minutes)</param>
    /// <Returns> TRUE if the date range is within the given TIME_INTERVAL, FALSE if the date range exceeds "TIME_INTERVAL"</Returns>
    public Boolean IsWithinValidWindow(Date StartTime, Date EndTime, int TimeInterval)
    {
        if (StartTime != null && EndTime != null) {
            long difference = Math.abs(EndTime.getTime() - StartTime.getTime()); //Diff should always be in positive since the order of arguments (start and end date) should not matter
            long DateDiff_hours = difference / (1000 * 60 * 60);
            System.out.println(DateDiff_hours);
            if (DateDiff_hours > (TimeInterval/60))
                return false;
            else
                return true;
        } else //If any one of them is null, consider it as a valid match since the other date is not available.
            return true;
    }
}
