#include "mainwindow.h"
#include "globals.h"

#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);

    //設定程式訊息
    QApplication::setOrganizationName(ExpenseBook::organization());
    QApplication::setOrganizationDomain(ExpenseBook::organizationDomain());
    QApplication::setApplicationName(ExpenseBook::application());

    MainWindow w;
    w.setFixedSize(800, 600);
    w.show();
    
    return a.exec();
}
