package com.example.loancalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loancalculator.ui.theme.LoanCalculatorTheme
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoanCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    LoanCalculator()
                }
            }//LoanCalculatorTheme
        }//setContent
    }//OnCreate
}//MainActivity

@Composable
fun LoanCalculator(){
    var loanTotalState by remember {
        mutableStateOf(0.0)
    }

    var customInterestRateState by remember {
        mutableStateOf(0.0f)
    }

    Column(modifier = Modifier.fillMaxSize()){
        LoanRow(
            loanTotal = loanTotalState,
            updateTotal = { newTotal ->
                loanTotalState = newTotal
            }
        )
        SliderRow(customInterestRateState, updateCustomInterest = { newCustomRate ->
            customInterestRateState = newCustomRate
        })
        HeadingRow()

        LoanCalcRow(loanTotalState, customInterestRateState)
    }//Column
}//LoanCalculator

@Composable
fun SliderRow(customInterestRate: Float, updateCustomInterest: (Float) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.End
        ) {

            Label(labelText = "Interest Rate:", align = TextAlign.End)

        } //column
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(2f),
            horizontalAlignment = Alignment.End
        ) {
            Slider(
                value = customInterestRate,
                onValueChange = {
                    updateCustomInterest(it)
                },
                valueRange = .0f .. 1.0f,
                onValueChangeFinished = {
                    //ignore
                },
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = colors.secondary,
                    activeTrackColor = colors.secondary
                )
            ) //Slider
        } //slider column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.End
        ) {

            Label(
                labelText = "${(customInterestRate*100f).toInt()}%",
                align = TextAlign.End
            )

        } //column
    }//Row
}

@Composable
fun LoanRow(loanTotal: Double, updateTotal: (Double) -> Unit){
    var badInput by remember {
        mutableStateOf(false)
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Label(labelText = "Loan Amount", align = TextAlign.Start)

        OutlinedTextField(
            value = loanTotal.toString(),
            label = {
                    Text(text = "Loan Total")
            },
            onValueChange = {
                val parsed = it.toDoubleOrNull() ?: 0.0

                if (parsed < 999999.99){
                    badInput = false
                    updateTotal(parsed)
                }else{
                    badInput = true
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.secondary,
                unfocusedBorderColor = MaterialTheme.colors.secondary,
                focusedLabelColor = MaterialTheme.colors.secondary,
                cursorColor = MaterialTheme.colors.primaryVariant
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = badInput,
            modifier = Modifier.fillMaxWidth()
        )//OutlinedTextField
    }//Row

}//LoanRow

@Composable
fun HeadingRow(){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ){
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Label(labelText = "Years", align = TextAlign.Center)
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Label(labelText = "EMI", align = TextAlign.Center)
        }
        Column(modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Label(labelText = "Total Amount", align = TextAlign.Center)
        }
    }//Row
}

@Composable
fun LoanCalcRow(loanTotal: Double, customInterestRate: Float){
    //val interestRate = customInterestRate/12.0f //may be 0.15
    val years = arrayOf(5,10,15,20,25,30)
        for (year in years){
            var emiValue = CalculateEMI(loanTotal, (customInterestRate/12.0f).toDouble(), year.toDouble())
            var totalAmount = CalculateTotalAmount(emiValue, year.toDouble())
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Label(labelText = year.toString(), align = TextAlign.Center)
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    EMIAndTotalValueField(value = emiValue, align = TextAlign.Center)
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    EMIAndTotalValueField(value = totalAmount, align = TextAlign.Center)
                }
            }//Row(for)

        }
}

@Composable
fun CalculateTotalAmount(EMI: Double, years: Double): Double{
    return EMI*12*years
}

@Composable
fun CalculateEMI(
    p: Double,
    r: Double,
    n: Double,
): Double{
    if (r == 0.0){
        return p/(12*n)
    }
    var divider = p*r*((1.0+r).pow(n))
    var dividend = ((1.0 + r).pow(n)) - 1
    return divider/dividend
}

@Composable
fun Label(labelText: String, align: TextAlign) {
    Text(
        text = labelText,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        textAlign = align,
        color = Color.Black,
        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    )
}//Label
@Composable
fun EMIAndTotalValueField(
    value: Double,
    align: TextAlign
) {
    BasicTextField(
        value = String.format("%.02f",value),
        onValueChange = {},
        enabled = false,
        textStyle = TextStyle(
            textAlign = align,
            color = Color.Black,
            fontSize = 18.sp
        )
    )
}//TipAndTotalValueField












