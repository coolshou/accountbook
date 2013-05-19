#include "core.h"
#include "globals.h"

#include <QSettings>
#include <QMessageBox>
#include <QDebug>

#include <QSqlQuery>
#include <QSqlError>
#include <QSqlQueryModel>

Core::Core(QObject *parent) 
    :QObject(parent), _categoryModel(new QSqlQueryModel(this))
{
    _database = QSqlDatabase::addDatabase("QSQLITE");
}


void Core::loadSettings()
{
    QSettings settings;
    if(!settings.contains(AccountBook::SETTINGS_DATABASE_PATH))
    {
        emit loadingSettingsFailed();
        return;
    }

    const QString databasePath = settings.value(AccountBook::SETTINGS_DATABASE_PATH).toString();
    setDatabasePath(databasePath);
}

void Core::loadDatabase()
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

    _categoryModel->setQuery("SELECT category FROM categories");

    emit databaseOnLoad();
}

QSqlQueryModel *Core::getCategoryModel() const
{
    return _categoryModel;
}

QString Core::getDatabasePath() const
{
    return _databasePath;
}

void Core::setDatabasePath(const QString &databasePath)
{
    //避免 loop
    if(_databasePath == databasePath)
        return;

    _databasePath = databasePath;

    QSettings settings;
    settings.setValue(AccountBook::SETTINGS_DATABASE_PATH, _databasePath);

    emit databasePathChanged(_databasePath);
}

Core::~Core()
{
    if(_database.isOpen())
        _database.close();
}

void Core::_createTables()
{
    QSqlQuery query;
    if(!query.exec(AccountBook::CREATE_EXPENSES_TABLE))
        qDebug() << _database.lastError();

    if(!query.exec(AccountBook::CREATE_CATEGORYIES_TABLE))
        qDebug() << _database.lastError();

    if(!query.exec(AccountBook::CREATE_INFO_TABLE))
        qDebug() << _database.lastError();
}
