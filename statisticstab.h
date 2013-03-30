#ifndef STATISTICSTAB_H
#define STATISTICSTAB_H

#include <QWidget>

namespace Ui {
class StatisticsTab;
}

class StatisticsTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit StatisticsTab(QWidget *parent = 0);
    ~StatisticsTab();
    
private:
    Ui::StatisticsTab *ui;
};

#endif // STATISTICSTAB_H
