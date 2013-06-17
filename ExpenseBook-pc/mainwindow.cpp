#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "globals.h"
#include "expensesqlmodel.h"
#include "expenselistitemdelegate.h"

#include <QKeyEvent>

#include <QSettings>
#include <QDir>
#include <QMessageBox>
#include <QFileDialog>

//SQL
#include <QtSQl>

//工具
#include <QTimer>
#include <QDebug>

MainWindow::MainWindow(QWidget *parent)
    :QWidget(parent), _ui(new Ui::MainWindow), _currentMode(NormalMode)
{
    _ui->setupUi(this);
    _ui->expenseWidget->hide();
    _ui->dateDateEdit->setDisplayFormat(ExpenseBook::dateFormat());
    _ui->dateDateEdit->setCalendarPopup(true);

    connect(_ui->addExpensePushButton, SIGNAL(clicked()), this, SLOT(_onAddExpenseButtonClicked()));
    connect(_ui->expenseListView, SIGNAL(clicked(QModelIndex)), this, SLOT(_onExpenseListItemSelected(QModelIndex)));
    connect(_ui->changeDatabasePathPushButton, SIGNAL(clicked()), this, SLOT(_onChangeDatabasePathButtonClicked()));
    connect(_ui->savePushButton, SIGNAL(clicked()), this, SLOT(_onSaveButtonClicked()));
    connect(_ui->deletePushButton, SIGNAL(clicked()), SLOT(_onDeleteButtonClicked()));
}

MainWindow::~MainWindow()
{
    delete _ui;
}

void MainWindow::showEvent(QShowEvent *)
{
    QTimer::singleShot(1000, this, SLOT(_prepareDatabase()));
}

void MainWindow::keyPressEvent(QKeyEvent *keyEvent)
{
    switch(keyEvent->key())
    {
    case Qt::Key_Escape: _returnNormalModel(); break;
    }
}

void MainWindow::_prepareDatabase()
{
    QString databasePath = _getDatabasePathFromSettings();
    if(databasePath.isEmpty())
    {
        databasePath = _getDatabasePathFromFileDialog();
        while(databasePath.isEmpty())
        {
            QMessageBox::about(NULL, "注意！", "請指定資料庫的位置和名稱喔");
            databasePath = _getDatabasePathFromFileDialog();
        }

        //設定資料庫位置
        QSettings settings;
        settings.setValue(ExpenseBook::databasePathSetting(), databasePath);
    }

    _ui->databasePathLineEdit->setText(databasePath);

    QSqlDatabase database = QSqlDatabase::addDatabase("QSQLITE");
    database.setDatabaseName(databasePath);
    if(!database.open())
        qDebug() << database.lastError();

    //其實應該要判斷有那些 table
    if(database.tables().isEmpty())
        _createDatabaseTables();

    _setExpenseListView();
}

void MainWindow::_onAddExpenseButtonClicked()
{
    _ui->expenseWidget->show();
    _ui->pictureLabel->setOriginPixmap(QPixmap(":/images/empty_picture.jpg"));
    _ui->spendLineEdit->clear();
    _ui->dateDateEdit->setDate(QDate::currentDate());

    QSqlQueryModel *categorySqlModel = new QSqlQueryModel(this);
    categorySqlModel->setQuery("SELECT category FROM categories");
    _ui->categoryComboBox->setModel(categorySqlModel);

    _ui->noteTextEdit->clear();

    _ui->deletePushButton->setDisabled(true);

    _currentMode = AddExpenseMode;
}

void MainWindow::_onExpenseListItemSelected(QModelIndex index)
{
    _ui->expenseWidget->show();
    ExpenseSqlModel *expenseSqlModel = qobject_cast<ExpenseSqlModel*>(_ui->expenseListView->model());

    _currentExpenseId = expenseSqlModel->data(index, ExpenseBook::IdRole).toLongLong();

    QPixmap picture = expenseSqlModel->data(index, ExpenseBook::PictureRole).value<QPixmap>();
    qlonglong spend = expenseSqlModel->data(index, ExpenseBook::SpendRole).toLongLong();

    QString dateString = expenseSqlModel->data(index, ExpenseBook::DateRole).toString();
    QString category = expenseSqlModel->data(index, ExpenseBook::CategoryRole).toString();
    QString note = expenseSqlModel->data(index, ExpenseBook::NoteRole).toString();

    _ui->pictureLabel->setOriginPixmap(picture);
    _ui->spendLineEdit->setText(QString::number(spend));
    _ui->dateDateEdit->setDate(QDate::fromString(dateString, ExpenseBook::dateFormat()));

    QSqlQueryModel *categorySqlModel = new QSqlQueryModel(this);
    categorySqlModel->setQuery("SELECT category FROM categories");
    _ui->categoryComboBox->setModel(categorySqlModel);

    for(int i=0; categorySqlModel->rowCount(); i++)
    {
        QModelIndex currentIndex = categorySqlModel->index(i, 0);
        if(categorySqlModel->data(currentIndex).toString() == category)
        {
            _ui->categoryComboBox->setCurrentIndex(i);
            break;
        }
    }

    _ui->noteTextEdit->setText(note);

    _ui->deletePushButton->setDisabled(false);

    _currentMode = EditExpenseMode;
}

void MainWindow::_onChangeDatabasePathButtonClicked()
{
    QString databasePath = _getDatabasePathFromFileDialog();
    if(databasePath.isEmpty())
        return;

    QSqlDatabase database = QSqlDatabase::database();
    database.setDatabaseName(databasePath);
    if(!database.open())
        qDebug() << database.lastError();

    //其實應該要判斷有那些 table
    if(database.tables().isEmpty())
        _createDatabaseTables();

    _setExpenseListView();
}

void MainWindow::_onSaveButtonClicked()
{
    if(_currentMode == NormalMode)
        qDebug() << "不可能是 NormalMode";

    QByteArray pictureBytes;
    QBuffer buffer(&pictureBytes);
    buffer.open(QIODevice::WriteOnly);
    _ui->pictureLabel->getOriginPixmap().save(&buffer, "JPG");

    qlonglong spend = _ui->spendLineEdit->text().toLongLong();
    QString dateString = _ui->dateDateEdit->text().trimmed();

    ExpenseSqlModel expenseSqlModel(this);
    qlonglong categoryId = expenseSqlModel.getCategoryIdfromCategory(_ui->categoryComboBox->currentText());
    if(categoryId == -1)
        qDebug() << "沒有這個分類" << _ui->categoryComboBox->currentText();

    QString note = _ui->noteTextEdit->toPlainText();
    QSqlQuery query;

    if(_currentMode == AddExpenseMode)
    {
        QString queryString =
                "INSERT INTO expenses (picture_bytes, spend, date_string, category_id, note) VALUES (?, ?, ?, ?, ?)";
        query.prepare(queryString);
        query.bindValue(0, pictureBytes);
        query.bindValue(1, spend);
        query.bindValue(2, dateString);
        query.bindValue(3, categoryId);
        query.bindValue(4, note);
    }
    else if(_currentMode == EditExpenseMode)
    {
        QString queryString =
                "UPDATE expenses SET picture_bytes=?, spend=?, date_string=?, category_id=?, note=? WHERE _id =?";
        query.prepare(queryString);
        query.bindValue(0, pictureBytes);
        query.bindValue(1, spend);
        query.bindValue(2, dateString);
        query.bindValue(3, categoryId);
        query.bindValue(4, note);
        query.bindValue(5, _currentExpenseId);
    }
    else
    {
        qDebug() << "沒有這個 currentModel" << _currentMode;
        return;
    }

    QSqlDatabase database = QSqlDatabase::database();
    if(!query.exec())
        qDebug() << database.lastError();

    _setExpenseListView();
    _ui->expenseWidget->hide();
}

void MainWindow::_onDeleteButtonClicked()
{
    QString queryString = "DELETE FROM expenses WHERE _id=?";
    QSqlQuery query(queryString);
    query.bindValue(0, _currentExpenseId);

    QSqlDatabase database = QSqlDatabase::database();
    if(!query.exec())
        qDebug() << database.lastError();

    _setExpenseListView();
    _ui->expenseWidget->hide();
}

QString MainWindow::_getDatabasePathFromSettings() const
{
    QSettings settings;
    if(settings.contains(ExpenseBook::databasePathSetting()))
        return settings.value(ExpenseBook::databasePathSetting()).toString();
    return QString();
}

QString MainWindow::_getDatabasePathFromFileDialog() const
{
    QFileDialog dialog;
    dialog.setAcceptMode(QFileDialog::AcceptSave);
    dialog.setFileMode(QFileDialog::AnyFile);
    dialog.setOption(QFileDialog::DontConfirmOverwrite);

    dialog.selectFile("accountbook.db");
    dialog.setWindowTitle("選擇資料庫的位置和名稱");

    QString databasePath;
    if(dialog.exec())
        databasePath = dialog.selectedFiles()[0];
    return databasePath;
}

void MainWindow::_returnNormalModel()
{
    _ui->expenseWidget->hide();
    _ui->expenseListView->clearSelection();

    _currentMode = NormalMode;
}

void MainWindow::_createDatabaseTables()
{
    QSqlDatabase database = QSqlDatabase::database();

    QSqlQuery query;
    if(!query.exec(ExpenseBook::createExpenseTable()))
        qDebug() << database.lastError();

    if(!query.exec(ExpenseBook::createCategoryTable()))
        qDebug() << database.lastError();

    //設定 default category
    if(!query.exec("insert into categories(category) values('未分類');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('飲食');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('衣物');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('住宿');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('行動');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('教育');"))
        qDebug() << database.lastError();
    if(!query.exec("insert into categories(category) values('娛樂');"))
        qDebug() << database.lastError();
}

void MainWindow::_setExpenseListView()
{
    ExpenseSqlModel *expenseModel = new ExpenseSqlModel(this);
    _ui->expenseListView->setModel(expenseModel);
    _ui->expenseListView->setItemDelegate(new ExpenseListItemDelegate(this));
}
