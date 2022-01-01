#ifndef COMMAND_SOCKET_H_
#define COMMAND_SOCKET_H_

namespace vp2 {

class EventManager;

class CommandSocket {
public:
    CommandSocket(EventManager & evtMgr);
    virtual ~CommandSocket();

private:
    int            listenFd;
    EventManager & eventManager;
};

} /* namespace vp2 */

#endif /* COMMAND_SOCKET_H_ */
