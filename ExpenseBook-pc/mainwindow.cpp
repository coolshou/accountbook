#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "globals.h"
#include "expensesqlmodel.h"

#include <QSettings>
#include <QDir>
#include <QMessageBox>
#include <QFileDialog>

//SQL
#include <QtSQl>

//工具
#include <QTimer>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent)
    :QWidget(parent), _ui(new Ui::MainWindow)
{
    _ui->setupUi(this);
    _ui->expenseWidget->hide();
}

MainWindow::~MainWindow()
{
    delete _ui;
}

void MainWindow::showEvent(QShowEvent *)
{
    QTimer::singleShot(1000, this, SLOT(_prepareDatabase()));
}

void MainWindow::_prepareDatabase()
{
    QString databasePath = _getDatabasePathFromSettings();
    if(databasePath.isEmpty())
        databasePath = _getDatabasePathFromFileDialog();

    QSqlDatabase database = QSqlDatabase::addDatabase("QSQLITE");
    database.setDatabaseName(databasePath);
    if(!database.open())
        qDebug() << database.lastError();

    //其實應該要判斷有那些 table
    if(database.tables().isEmpty())
        _createDatabaseTables();

    _setExpenseListView();
}

QString MainWindow::_getDatabasePathFromSettings() const
{
    QSettings settings;
    if(settings.contains(ExpenseBook::databasePathSetting()))
        return settings.value(ExpenseBook::databasePathSetting()).toString();
    return QString();
}

QString MainWindow::_getDatabasePathFromFileDialog() const
{
    QString databasePath = QFileDialog::getOpenFileName(NULL, "選擇資料庫的位置和名稱", QDir::homePath() + "/accountbook.db");
    while(databasePath.isEmpty())
    {
        QMessageBox::about(NULL, "注意！", "請指定資料庫的位置和名稱喔");
        databasePath = QFileDialog::getOpenFileName(NULL, "選擇資料庫的位置和名稱", QDir::homePath() + "/accountbook.db");
    }

    //設定資料庫位置
    QSettings settings;
    settings.setValue(ExpenseBook::databasePathSetting(), databasePath);

    return databasePath;
}

void MainWindow::_createDatabaseTables()
{
    QSqlDatabase database;

    QSqlQuery query;
    if(!query.exec(ExpenseBook::createExpenseTable()))
        qDebug() << database.lastError();

    if(!query.exec(ExpenseBook::createCategoryTable()))
        qDebug() << database.lastError();

    //設定 default category
    if(!query.exec("insert into categories(category) values('未分類');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('飲食');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('衣物');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('住宿');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('行動');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('教育');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('娛樂');"))
        qDebug() << database.lastError();
}

void MainWindow::_setExpenseListView()
{
    ExpenseSqlModel *expenseModel = new ExpenseSqlModel(this);
    expenseModel->setQuery("SELECT spend, picture_bytes FROM expenses;");
    qDebug() << expenseModel->rowCount();
    _ui->expenseListView->setModel(expenseModel);
}
