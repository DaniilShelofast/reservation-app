package bot.reservation.validate;

public final class PhoneNormalizer {
    private PhoneNormalizer() {
    }

    public static String normalize(String rawPhone) {
        if (rawPhone == null) {
            return "";
        }
        String digits = rawPhone.replaceAll("\\D", "");
        // прибираємо код країни (380) або трунк-нуль, лишаємо останні 9 цифр
        if (digits.length() > 9) {
            digits = digits.substring(digits.length() - 9);
        }
        return digits;
    }

    public static boolean matches(String phoneA, String phoneB) {
        String a = normalize(phoneA);
        String b = normalize(phoneB);
        return !a.isEmpty() && a.equals(b);
    }
}
