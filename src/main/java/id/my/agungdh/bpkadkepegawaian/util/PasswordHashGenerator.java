package id.my.agungdh.bpkadkepegawaian.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java PasswordHashGenerator <password>");
            System.out.println("Example: mvn exec:java -Dexec.mainClass=\"id.my.agungdh.bpkadkepegawaian.util.PasswordHashGenerator\" -Dexec.args=\"admin123\"");
            System.exit(1);
        }

        String password = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);

        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
    }
}
