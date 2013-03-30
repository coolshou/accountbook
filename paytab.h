#ifndef PAYTAB_H
#define PAYTAB_H

#include <QWidget>

namespace Ui {
class PayTab;
}

class PayTab : public QWidget
{
    Q_OBJECT
    
public:
    explicit PayTab(QWidget *parent = 0);
    ~PayTab();
    
private:
    Ui::PayTab *ui;
};

#endif // PAYTAB_H
