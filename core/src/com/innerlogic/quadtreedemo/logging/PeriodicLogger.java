package com.innerlogic.quadtreedemo.logging;

import com.badlogic.gdx.utils.Array;

/**
 * Created with IntelliJ IDEA.
 * User: whita03
 * Date: 10/10/13
 * Time: 4:07 PM
 */
public class PeriodicLogger
{
    private long startTime;
    private long intervalInSeconds;
    private Array<LoggingAction> loggingActions;

    public PeriodicLogger()
    {
        this(1);
    }

    public PeriodicLogger(long interval)
    {
        // Default to 1 second if given something lower
        if(interval < 0)
        {
            interval = 1000;
        }

        intervalInSeconds = interval;
        startTime = System.currentTimeMillis();

        loggingActions = new Array<LoggingAction>(true, 10);
    }

    public void log()
    {
        if(System.currentTimeMillis() - startTime > (intervalInSeconds * 1000L))
        {
            // Reset the start time
            startTime = System.currentTimeMillis();

            // Perform all logging actions
            for(LoggingAction currAction : loggingActions)
            {
                currAction.doAction();
            }
        }
    }

    public void addLoggingAction(LoggingAction action)
    {
        loggingActions.add(action);
    }

    public void clearLogginActions()
    {
        loggingActions.clear();
    }
}
