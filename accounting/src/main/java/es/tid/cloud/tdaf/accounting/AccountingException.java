package es.tid.cloud.tdaf.accounting;

public class AccountingException extends Exception {

    private static final long serialVersionUID = 1786283658581542451L;
    
    private Code code;

    public AccountingException(Code code, String details) {
        super(String.format(
                "Error %s , %s\nError details : %s", code.name(), code.getMsg(), details));
        this.code = code;
    }

    public AccountingException(Code code, String details, Throwable cause) {
        super(String.format(
                "Error %s , %s\nError details : %s", code.name(), code.getMsg(), details), cause);
        this.code = code;
    }

    public AccountingException(Code code) {
        super(String.format(
                "Error %s , %s", code.name(), code.getMsg()));
        this.code = code;
    }

    public AccountingException(String details, Throwable cause) {
        this(Code.AC_0000, details, cause);
    }

    public AccountingException(String details) {
        this(Code.AC_0000, details);
    }

    public AccountingException(Throwable cause) {
        super(String.format(
                "Error %s , %s", Code.AC_0000, Code.AC_0000.getMsg()), cause);
        this.code = Code.AC_0000;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public enum Code {

        AC_0000("Unexpected error"),
        AC_0001("Not found \"" + Constants.SYS_LOG_DIR + "\" system property."),
        AC_0002("There are invalid entries in CSV file");

        private String msg;
        Code(String msg){
            this.msg = msg;
        }

        public String getMsg(){
            return this.msg;
        }
    }
}
