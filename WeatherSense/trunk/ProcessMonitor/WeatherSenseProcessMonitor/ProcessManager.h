#ifndef PROCESSMANAGER_H
#define	PROCESSMANAGER_H

#include <string>

class ProcessManager {
public:
    static ProcessManager & getInstance();
    void initialize(std::string & configFile);
    bool startProcesses();
    bool stopProcesses();

private:
    ProcessManager();
    virtual ~ProcessManager();

    static ProcessManager * processManager;

};

#endif	/* PROCESSMANAGER_H */

