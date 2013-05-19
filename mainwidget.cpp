#include "mainwidget.h"
#include "ui_mainwidget.h"
#include "globals.h"
#include "strings.h"
#include "core.h"
#include "settingtab.h"

#include <QDebug>
#include <QSettings>
#include <QMessageBox>

MainWidget::MainWidget(QWidget *parent)
    :QWidget(parent), _ui(new Ui::MainWidget), _core(new Core(parent))
{
    _ui->setupUi(this);

    connect(_core, &Core::databasePathChanged, _ui->settingTab, &SettingTab::setDatabasePath);
    connect(_ui->settingTab, &SettingTab::databasePathChanged, _core, &Core::setDatabasePath);

    connect(_core, &Core::loadingSettingsFailed, this, &MainWidget::_onloadingSettingsFailed);
    connect(_core, &Core::databasePathChanged, this, &MainWidget::_onDatabasePathChanged);
    connect(_core, &Core::databaseOnLoad, this, &MainWidget::_onDatabaseLoad);

    //讀取設定
    _core->loadSettings();
}



MainWidget::~MainWidget()
{
    delete _ui;
}

void MainWidget::_onDatabasePathChanged()
{
    //讀取資料庫
    _core->loadDatabase();
}

void MainWidget::_onloadingSettingsFailed()
{
    QMessageBox::about(NULL, AccountBook::welcomeMessageTitle(), AccountBook::welcomeMessageContent());
    _ui->tabWidget->setCurrentIndex(AccountBook::SETTING_TAB);
    _ui->settingTab->onChangeDatabasePushButtonClicked();
}

void MainWidget::_onDatabaseLoad()
{
    _ui->epenseTab->setCategoryModel(_core->getCategoryModel());
}
