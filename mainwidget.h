#ifndef MAINWIDGET_H
#define MAINWIDGET_H

#include <QWidget>

class Core;

namespace Ui
{
    class MainWidget;
}

class MainWidget : public QWidget
{
    Q_OBJECT
    
public:
    explicit MainWidget(QWidget *parent = 0);
    ~MainWidget();

private:
    Ui::MainWidget *_ui;
    Core *_core;
};

#endif // MAINWIDGET_H
