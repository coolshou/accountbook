#ifndef CORE_H
#define CORE_H

#include <QObject>
#include <QSqlDatabase>

class QString;

class Core : public QObject
{
    Q_OBJECT

public:

    explicit Core(QObject *parent = 0);

    void loadSettings();
    void loadDatabase();

    QString getDatabasePath() const;
    void setDatabasePath(const QString& databasePath);

    ~Core();

signals:

    void loadingSettingsFailed();
    void databasePathChanged(const QString& databasePath);
    void databaseOnLoad();

private:

    QString _databasePath;
    QSqlDatabase _database;

    void _createTables();
};

#endif // CORE_H
