package lk.dialog.iot.pcs.util;

public class EnumTypes {

    public enum OperationTypes {

        HTTP_TO_BROKER_OPERATION("httpCalltoBroker"),
        TO_EXTERNAL_OPERATION("toExternalService"),
        SCHEDULE__OPERATION("schedule");

        private final String name;

        private OperationTypes(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
