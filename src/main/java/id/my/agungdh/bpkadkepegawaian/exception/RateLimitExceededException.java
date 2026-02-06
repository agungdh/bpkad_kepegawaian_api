package id.my.agungdh.bpkadkepegawaian.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
