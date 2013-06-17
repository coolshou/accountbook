#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QWidget>
#include <QModelIndex>

namespace Ui
{
    class MainWindow;
}

class MainWindow : public QWidget
{
    Q_OBJECT
    
public:

    enum Mode { NormalMode, AddExpenseMode, EditExpenseMode };

    explicit MainWindow(QWidget *parent = 0);

    ~MainWindow();
    
protected:

    void showEvent(QShowEvent *);
    void keyPressEvent(QKeyEvent *keyEvent);

private slots:

    void _prepareDatabase();
    void _onAddExpenseButtonClicked();
    void _onExpenseListItemSelected(QModelIndex index);
    void _onChangeDatabasePathButtonClicked();
    void _onSaveButtonClicked();
    void _onDeleteButtonClicked();

private:
    Ui::MainWindow *_ui;
    Mode _currentMode;
    qlonglong _currentExpenseId;

    QString _getDatabasePathFromSettings() const;
    QString _getDatabasePathFromFileDialog() const;

    void _returnNormalModel();
    void _createDatabaseTables();
    void _setExpenseListView();
};

#endif // MAINWINDOW_H
