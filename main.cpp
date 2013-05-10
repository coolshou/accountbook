#include <QApplication>
#include "mainwidget.h"
#include "globals.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    //設定程式訊息
    QApplication::setOrganizationName(ORGANIZATION);
    QApplication::setOrganizationDomain(ORGANIZATION_DOMAIN);
    QApplication::setApplicationName(APPLICATION);

    MainWidget w;
    w.show();
    
    return a.exec();
}
