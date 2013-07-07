package net.nctucs.lazchi.marco79423.ExpenseBook;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalculatorFragment extends DialogFragment
{
	static CalculatorFragment newInstance(long value)
	{
		CalculatorFragment calculatorFragment = new CalculatorFragment();

		Bundle args = new Bundle();
		args.putLong("value", value);
		calculatorFragment.setArguments(args);

		return calculatorFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//long value = getArguments().getLong("value");
	}
/*
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.expense_calculator, container, false);

		return view;
	}*/


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		//builder.setMessage("test message");
		//builder.setTitle("test title");
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.expense_calculator, null);
		builder.setView(view);

		return builder.create();
	}
}
