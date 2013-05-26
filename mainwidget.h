#ifndef MAINWIDGET_H
#define MAINWIDGET_H

#include <QWidget>
#include <QSqlDatabase>

namespace Ui
{
    class MainWidget;
}

class MainWidget : public QWidget
{
    Q_OBJECT
    
public:
    explicit MainWidget(QWidget *parent = 0);
    void setDatabase(const QString &databasePath);
    ~MainWidget();

signals:

    void databasePathChanged(const QString& databasePath);
    void databaseOnLoad();

private:
    Ui::MainWidget *_ui;

    QString _databasePath;
    QSqlDatabase _database;
    QHash<QString, int> _databaseInfo;

    void _loadDatabase();
    void _createTables();
};

#endif // MAINWIDGET_H
