#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QWidget>

namespace Ui
{
    class MainWindow;
}

class MainWindow : public QWidget
{
    Q_OBJECT
    
public:
    explicit MainWindow(QWidget *parent = 0);

    ~MainWindow();
    
protected:

    void showEvent(QShowEvent *);

private slots:

    void _prepareDatabase();

private:
    Ui::MainWindow *_ui;

    QString _getDatabasePathFromSettings() const;
    QString _getDatabasePathFromFileDialog() const;

    void _createDatabaseTables();
    void _setExpenseListView();
};

#endif // MAINWINDOW_H
