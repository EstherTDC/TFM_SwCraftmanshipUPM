package es.tfm.bingo.utils;

public class ExceptionErrorInApp extends Exception{


		private static final long serialVersionUID = 7718828512143293559L;

		private final Result code;

		public ExceptionErrorInApp(Result code) {
			super(code.get());
			this.code = code;
		}

		public ExceptionErrorInApp(String message, Result code) {
			super(message);
			this.code = code;
		}

		public Result getCode() {
			return this.code;
		}
}
