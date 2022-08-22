#include <QApplication>
#include "mainwidget.h"
#include "accountbook.h"

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    //設定程式訊息
    QApplication::setOrganizationName(AccountBook::organization());
    QApplication::setOrganizationDomain(AccountBook::organizationDomain());
    QApplication::setApplicationName(AccountBook::application());

    MainWidget w;
    w.show();

    return a.exec();
}
