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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.List;

public class CalculatorFragment extends DialogFragment
{
	private TextView _resultTextView;

	public enum CalculatorMode {NormalMode, PlusMode, MultiplyMode};
	private CalculatorMode _calculatorMode;

	private BigInteger _value, _holdValue;

	static CalculatorFragment newInstance(String value)
	{
		CalculatorFragment calculatorFragment = new CalculatorFragment();

		Bundle args = new Bundle();
		args.putString("value", value);
		calculatorFragment.setArguments(args);

		return calculatorFragment;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		_value = new BigInteger((String)getArguments().get("value"));
		_holdValue = new BigInteger("0");

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.expense_calculator, null);

		_calculatorMode = CalculatorMode.NormalMode;

		_resultTextView = (TextView)view.findViewById(R.id.calculator_view_result);
		_resultTextView.setText(String.valueOf(_value));

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
				R.id.calculator_button_plus,
				R.id.calculator_button_multiply,
				R.id.calculator_button_equal,
				R.id.calculator_button_clear
		};

		for(int i=0; i < buttonIdList.length; i++)
		{
			Button button = (Button)view.findViewById(buttonIdList[i]);
			switch(i)
			{
				case 0:	case 1:	case 2:
				case 3:	case 4:	case 5:
				case 6:	case 7:	case 8:
				case 9: button.setOnClickListener(new CalculatorOnClickListener(String.valueOf(i))); break;
				case 10: button.setOnClickListener(new CalculatorOnClickListener("plus")); break;
				case 11: button.setOnClickListener(new CalculatorOnClickListener("multiply")); break;
				case 12: button.setOnClickListener(new CalculatorOnClickListener("equal")); break;
				case 13: button.setOnClickListener(new CalculatorOnClickListener("clear")); break;
			}
		}

		ImageButton imageButton = (ImageButton) view.findViewById(R.id.calculator_button_back);
		imageButton.setOnClickListener(new CalculatorOnClickListener("back"));

		//確認按鈕
		Button confirmButton = (Button) view.findViewById(R.id.calculator_button_confirm);
		confirmButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				MainActivity mainActivity = (MainActivity)getActivity();
				if(_calculatorMode == CalculatorMode.NormalMode)
					mainActivity.getData(new BigInteger(_resultTextView.getText().toString()));
				else if(_calculatorMode == CalculatorMode.PlusMode)
					mainActivity.getData(_holdValue.add(_value));
				else
					mainActivity.getData(_holdValue.multiply(_value));
				CalculatorFragment.this.getFragmentManager().beginTransaction().remove(CalculatorFragment.this).commit();
			}
		});

		//取消按鈕
		Button cancelButton = (Button) view.findViewById(R.id.calculator_button_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				CalculatorFragment.this.getFragmentManager().beginTransaction().remove(CalculatorFragment.this).commit();
			}
		});

		builder.setView(view);

		return builder.create();
	}

	class CalculatorOnClickListener implements View.OnClickListener
	{
		private String _key;
		private CalculatorMode _mode;

		//for equal
		private BigInteger _lastValue;
		private CalculatorMode _lastMode;

		CalculatorOnClickListener(String key)
		{
			_key = key;
			_lastValue = new BigInteger("0");
			_lastMode = CalculatorMode.NormalMode;

			if(key.equals("plus"))
				_mode = CalculatorMode.PlusMode;
			else if(key.equals("multiply"))
				_mode = CalculatorMode.MultiplyMode;
			else
				_mode = CalculatorMode.NormalMode;
		}

		@Override
		public void onClick(View view)
		{
			if(_key.matches("\\d")) //如果是數字的話
			{
				if(_value.equals(new BigInteger("0")))
					_value = new BigInteger(_key);
				else
					_value = _value.multiply(new BigInteger("10")).add(new BigInteger(_key));
				_resultTextView.setText(_value.toString());
			}
			else if(_key.equals("back"))
			{
				String resultString = _resultTextView.getText().toString();
				if(resultString.length() > 0)
					resultString = resultString.substring(0, resultString.length() - 1);

				_resultTextView.setText(resultString);
				_value = new BigInteger((resultString.length() > 0) ? resultString : "0");
			}
			else if(_key.equals("equal"))
			{

				if(_calculatorMode != CalculatorMode.NormalMode)
				{
					_lastMode = _calculatorMode;
					_lastValue = _value;

					_calculatorMode = CalculatorMode.NormalMode;
				}

				if(_lastMode == CalculatorMode.PlusMode)
					_holdValue = _holdValue.add(_lastValue);
				else if(_lastMode == CalculatorMode.MultiplyMode)
					_holdValue = _holdValue.multiply(_lastValue);

				_value = new BigInteger("0");
				_resultTextView.setText(_holdValue.toString());
			}
			else if(_key.equals("clear"))
			{
				_value = new BigInteger("0");
				_holdValue = new BigInteger("0");
				_resultTextView.setText(String.valueOf(_value));
			}
			else if(_key.equals("plus") || _key.equals("multiply"))
			{
				if(_calculatorMode != CalculatorMode.NormalMode)
				{
					if(_lastMode == CalculatorMode.PlusMode)
						_holdValue = _holdValue.add(_lastValue);
					else if(_lastMode == CalculatorMode.MultiplyMode)
						_holdValue = _holdValue.multiply(_lastValue);
				}
				else
				{
					_holdValue = _value;
				}

				_value = new BigInteger("0");
				_calculatorMode = _mode;
				_resultTextView.setText(_holdValue.toString());

			}
		}
	};
}
