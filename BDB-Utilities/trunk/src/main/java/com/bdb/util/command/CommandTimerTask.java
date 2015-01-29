package com.bdb.util.command;

import java.util.*;
import java.util.logging.*;

/**
 * A TimerTaskCommand is used to schedule a command to be sent to a CommandReceiver after a period of time.
 *
 * @author Bruce
 */
public class CommandTimerTask extends TimerTask {
    private final String name;
    private final CommandReceiver receiver;
    private final Command command;
    private static final Logger logger = Logger.getLogger(CommandTimerTask.class.getName());

    /**
     * Constructor.
     *
     * @param name The name of the timer task for logger purposes
     * @param receiver The receiver to which the command will be sent when the timer expires
     * @param command The command that will be sent to the receiver when the timer expires
     */
    public CommandTimerTask(String name, CommandReceiver receiver, Command command) {
        logger.log(Level.FINER, "Constructing TimerTask {0}", name);
        this.name = name;
        this.command = command;
        this.receiver = receiver;
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        logger.log(Level.FINE, "Sending command for timer task {0}", name);
        receiver.sendCommand(command);
    }

    /*
     * (non-Javadoc)
     * @see java.util.TimerTask#cancel()
     */
    @Override
    public boolean cancel() {
        logger.log(Level.FINE, "Cancelling Timer Task {0}", name);
        return super.cancel();
    }
}