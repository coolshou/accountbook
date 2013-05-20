#include "expensetab.h"
#include "ui_expensetab.h"

#include <QDebug>
#include <QSqlQueryModel>
#include <QSqlRelationalTableModel>
#include <QCalendarWidget>

ExpenseTab::ExpenseTab(QWidget *parent)
    :QWidget(parent), _ui(new Ui::ExpenseTab), _categoryModel(new QSqlQueryModel(this)), _expenseModel(nullptr)
{
    _ui->setupUi(this);

    connect(_ui->calendarWidget, &QCalendarWidget::clicked, _ui->dateEdit, &QDateEdit::setDate);
    connect(_ui->dateEdit, &QDateEdit::dateChanged, _ui->calendarWidget, &QCalendarWidget::setSelectedDate);

    _ui->dateEdit->setDate(QDate::currentDate());

    _ui->expenseTableView->setSelectionBehavior(QTableView::SelectRows);
    _ui->expenseTableView->setSelectionMode(QTableView::SingleSelection);
    _ui->expenseTableView->verticalHeader()->hide();
    _ui->expenseTableView->horizontalHeader()->setStretchLastSection(true);
}

void ExpenseTab::loadData()
{
    //category
    _categoryModel->setQuery("SELECT category FROM categories");
    _ui->categoryListView->setModel(_categoryModel);

    //expenses
    _expenseModel = new QSqlRelationalTableModel(this);
    _expenseModel->setTable("expenses");

    _expenseModel->setHeaderData(1, Qt::Horizontal, "照片");
    _expenseModel->setHeaderData(2, Qt::Horizontal, "金額");
    _expenseModel->setHeaderData(3, Qt::Horizontal, "日期");
    _expenseModel->setHeaderData(4, Qt::Horizontal, "分類");
    _expenseModel->setHeaderData(5, Qt::Horizontal, "筆記");
    _expenseModel->setSort(0, Qt::DescendingOrder);
    _expenseModel->setRelation(4, QSqlRelation("categories", "_id", "category"));
    _expenseModel->select();
    _ui->expenseTableView->setModel(_expenseModel);
    _ui->expenseTableView->setColumnHidden(0, true);
}

ExpenseTab::~ExpenseTab()
{
    delete _ui;
}
