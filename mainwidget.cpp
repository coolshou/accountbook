#include "mainwidget.h"
#include "ui_mainwidget.h"
#include "globals.h"
#include "settingtab.h"

#include <QSettings>
#include <QMessageBox>

MainWidget::MainWidget(QWidget *parent) :
    QWidget(parent),
    _ui(new Ui::MainWidget)
{
    _ui->setupUi(this);
    _checkSettings();
}

MainWidget::~MainWidget()
{
    delete _ui;
}

void MainWidget::_checkSettings()
{
    QSettings settings;
    if(!settings.contains(SETTING_DATABASE_PATH))
    {
        QMessageBox::about(NULL, WELCOME_MESSAGE_TITLE, WELCOME_MESSAGE_CONTENT);
        _ui->tabWidget->setCurrentIndex(SETTING_TAB);
        _ui->settingTab->onChangeDatabasePushButtonClicked();
    }
}
