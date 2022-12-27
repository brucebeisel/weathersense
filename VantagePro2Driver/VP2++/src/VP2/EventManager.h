/*
 * Copyright (C) 2022 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#ifndef EVENT_MANAGER_H_
#define EVENT_MANAGER_H_

#include <queue>
#include <string>
#include <mutex>

namespace vp2 {

/**
 * Class to handle events from the HTTP threads.
 */
class EventManager {
public:
    /**
     * Constructor.
     */
    EventManager();

    /**
     * Destructor.
     */
    virtual ~EventManager();

    /**
     * Check if there is an event on the queue. Note that in a multi-threaded environment
     * the return value may no longer be valid when the consumeEvent() method is called.
     *
     * @return True if the queue is not empty at the moment
     */
    bool isEventAvailable() const;

    /**
     * Queue an event.
     *
     * @param event The event to be queued
     */
    void queueEvent(const std::string & event);

    /**
     * Consume the event at the head of the queue.
     *
     * @param event The event that was copied from the head of the queue
     *
     * @return True if an event was actually copied. If false, the parameter event is not changed.
     */
    bool consumeEvent(std::string & event);

    //
    // Prevent all copying and moving
    //
    EventManager(const EventManager &) = delete;
    EventManager & operator=(const EventManager &) = delete;

private:
    std::queue<std::string> commandQueue; // The queue on which to store events
    mutable std::mutex mutex;             // The mutex to protect the queue against multi-threaded contention
};

} /* namespace vp2 */

#endif /* EVENT_MANAGER_H_ */
