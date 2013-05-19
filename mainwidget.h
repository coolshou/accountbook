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

    void _onDatabasePathChanged();
    void _onloadingSettingsFailed();
    void _onDatabaseLoad();
};

#endif // MAINWIDGET_H
