#ifndef CORE_H
#define CORE_H

#include <QObject>
#include <QSqlDatabase>

class QString;
class QSqlQueryModel;
class QSqlTableModel;

class Core : public QObject
{
    Q_OBJECT

public:

    explicit Core(QObject *parent = 0);

    void loadSettings();
    void loadDatabase();

    QSqlQueryModel *getCategoryModel() const;

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

    QSqlQueryModel *_categoryModel;

    void _createTables();
};

#endif // CORE_H
