#ifndef SETTINGTAB_H
#define SETTINGTAB_H

#include <QWidget>

namespace Ui {
class SettingTab;
}

class SettingTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit SettingTab(QWidget *parent = 0);
    ~SettingTab();
    
private:
    Ui::SettingTab *ui;
};

#endif // SETTINGTAB_H
