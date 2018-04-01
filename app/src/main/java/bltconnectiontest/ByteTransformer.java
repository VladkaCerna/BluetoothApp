package bltconnectiontest;

import flexjson.transformer.AbstractTransformer;

import static java.security.AccessController.getContext;

/**
 * Created by cernav1 on 16.3.2018.
 */

public class ByteTransformer extends AbstractTransformer {
    @Override
    public void transform(Object object) {
        if(object instanceof Byte)
        {
            String val = String.format("%d", (byte)object & 0xFF);
            getContext().write(val);
        }
        else
        {
            getContext().writeQuoted(object.toString());
        }
    }
}
