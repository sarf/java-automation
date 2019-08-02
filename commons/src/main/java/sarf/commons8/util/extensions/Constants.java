package sarf.commons8.util.extensions;

import java.util.function.BooleanSupplier;

class Constants {

    static final BooleanSupplier always = () -> true;
    static final BooleanSupplier never = () -> false;

    private Constants() {
        throw new UnsupportedOperationException();
    }

}
