#include "mainwidget.h"
#include "ui_mainwidget.h"
#include "globals.h"
#include "strings.h"
#include "settingtab.h"

#include <QDebug>
#include <QSettings>
#include <QMessageBox>
#include <QFileDialog>

#include <QtSql>


MainWidget::MainWidget(QWidget *parent)
    :QWidget(parent), _ui(new Ui::MainWidget)
{
    _ui->setupUi(this);

    _database = QSqlDatabase::addDatabase("QSQLITE");

    QSettings settings;
    QString databasePath;
    if(settings.contains(AccountBook::SETTINGS_DATABASE_PATH))
        databasePath = settings.value(AccountBook::SETTINGS_DATABASE_PATH).toString();
    while(databasePath.isEmpty())
    {
        QMessageBox::about(NULL, AccountBook::welcomeMessageTitle(), AccountBook::welcomeMessageContent());
        databasePath = QFileDialog::getOpenFileName(this, "選擇資料庫的位置和名稱", QDir::homePath() + "/accountbook.db");
    }
    setDatabase(databasePath);

    connect(_ui->settingTab, &SettingTab::databasePathChanged, this, &MainWidget::setDatabase);
}

void MainWidget::setDatabase(const QString &databasePath)
{
    //避免 loop
    if(_databasePath == databasePath)
        return;

    _databasePath = databasePath;
    QSettings settings;
    settings.setValue(AccountBook::SETTINGS_DATABASE_PATH, _databasePath);

    _ui->settingTab->setDatabasePath(_databasePath);
    _loadDatabase();
}

MainWidget::~MainWidget()
{
    if(_database.isOpen())
        _database.close();
    delete _ui;
}

void MainWidget::_loadDatabase()
{
    if(_database.isOpen())
        _database.close();

    _database.setDatabaseName(_databasePath);

    if(!_database.open())
    {
        qDebug() << _database.lastError();
        return;
    }

    if(_database.tables().isEmpty())
        _createTables();

    _ui->epenseTab->loadData();
    _ui->settingTab->loadData();
}

void MainWidget::_createTables()
{
    QSqlQuery query;
    if(!query.exec(AccountBook::CREATE_EXPENSES_TABLE))
        qDebug() << _database.lastError();

    if(!query.exec(AccountBook::CREATE_CATEGORYIES_TABLE))
        qDebug() << _database.lastError();

    if(!query.exec(AccountBook::CREATE_INFO_TABLE))
        qDebug() << _database.lastError();

    //設定 default category
    if(!query.exec("insert into categories(category) values('食');"))
        qDebug() << _database.lastError();
    if(!query.exec("insert into categories(category) values('衣');"))
        qDebug() << _database.lastError();
    if(!query.exec("insert into categories(category) values('住');"))
        qDebug() << _database.lastError();
    if(!query.exec("insert into categories(category) values('行');"))
        qDebug() << _database.lastError();
    if(!query.exec("insert into categories(category) values('育');"))
        qDebug() << _database.lastError();
    if(!query.exec("insert into categories(category) values('樂');"))
        qDebug() << _database.lastError();

}

