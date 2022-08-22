#ifndef SETTINGTAB_H
#define SETTINGTAB_H

#include <QWidget>

namespace Ui
{
    class SettingTab;
}

class SettingTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit SettingTab(QWidget *parent = 0);

    void setDatabasePath(const QString &databasePath);
    void onChangeDatabasePushButtonClicked();
    void loadData();

    ~SettingTab();

signals:

    void databasePathChanged(const QString&);

private:

    Ui::SettingTab *_ui;

    void _onAddCategoryPushButtonClicked();
    void _onDeleteCategoryPushButtonClicked();
};

#endif // SETTINGTAB_H
