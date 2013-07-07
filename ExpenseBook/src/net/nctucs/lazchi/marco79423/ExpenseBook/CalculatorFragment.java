package net.nctucs.lazchi.marco79423.ExpenseBook;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CalculatorFragment extends DialogFragment
{
	private TextView _resultTextView;

	/*private Button backButton;
	private List<Button> numberButtons;
	private Button dotButton;
	private Button confirmButton;
	Button cancelButton;*/

	static CalculatorFragment newInstance(long value)
	{
		CalculatorFragment calculatorFragment = new CalculatorFragment();

		Bundle args = new Bundle();
		args.putLong("value", value);
		calculatorFragment.setArguments(args);

		return calculatorFragment;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		long value = (Long)getArguments().get("value");

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.expense_calculator, null);

		_resultTextView = (TextView)view.findViewById(R.id.calculator_view_result);
		_resultTextView.setText(String.valueOf(value));

		//設定計算機的按鍵
		int[] buttonIdList = new int[] {
				R.id.calculator_button_0,
				R.id.calculator_button_1,
				R.id.calculator_button_2,
				R.id.calculator_button_3,
				R.id.calculator_button_4,
				R.id.calculator_button_5,
				R.id.calculator_button_6,
				R.id.calculator_button_7,
				R.id.calculator_button_8,
				R.id.calculator_button_9,
				R.id.calculator_button_00,
				R.id.calculator_button_plus,
				R.id.calculator_button_minus,
				R.id.calculator_button_multiply,
				R.id.calculator_button_back
		};

		for(int i=0; i < buttonIdList.length - 1; i++)
		{
			Button button = (Button)view.findViewById(buttonIdList[i]);
			switch(i)
			{
				case 0:	case 1:	case 2:
				case 3:	case 4:	case 5:
				case 6:	case 7:	case 8:
				case 9: button.setOnClickListener(new CalculatorOnClickListener(String.valueOf(i))); break;
				case 10: button.setOnClickListener(new CalculatorOnClickListener("00")); break;
				case 11: button.setOnClickListener(new CalculatorOnClickListener("+")); break;
				case 12: button.setOnClickListener(new CalculatorOnClickListener("-")); break;
				case 13: button.setOnClickListener(new CalculatorOnClickListener("x")); break;
			}
		}

		builder.setView(view);

		return builder.create();
	}

	class CalculatorOnClickListener implements View.OnClickListener
	{
		private String _value;

		CalculatorOnClickListener(String value)
		{
			_value = value;
		}

		@Override
		public void onClick(View view)
		{
			String result = _resultTextView.getText().toString();

			if(result.equals("0"))
			{
				if(!_value.equals("0") && !_value.equals("00") && !_value.equals("+") && !_value.equals("-") && !_value.equals("x"))
					_resultTextView.setText(_value);
			}
			else
				_resultTextView.setText(result + _value);
		}
	};
}
