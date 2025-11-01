object Utils {
    fun calculateBMI(heightCm: Double, weightKg: Double): Double {
        if (heightCm <= 0.0) return 0.0
        val heightM = heightCm / 100.0
        val bmi = weightKg / (heightM * heightM)
        return (Math.round(bmi * 100.0) / 100.0) // round to 2 decimals
    }

    fun bmiStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            else -> "Overweight"
        }
    }

    fun calculateAge(dobMillis: Long): Int {
        val dob = Calendar.getInstance().apply { timeInMillis = dobMillis }
        val now = Calendar.getInstance()
        var age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }
}
