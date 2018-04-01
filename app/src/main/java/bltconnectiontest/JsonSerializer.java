package bltconnectiontest;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import java.util.Date;

/**
 * Created by cernav1 on 16.3.2018.
 */

//flexjson serializer utilized to transform also dates and bytes between Java and C#
public class JsonSerializer {
    
    public String SerializeToJson(Object o)
    {
        JSONSerializer serializer = new JSONSerializer().transform(new ByteTransformer(), Byte.class).transform(new DateTransformer("yyyy-MM-dd'T'HH:mm:ss.SSS"), Date.class);
        String jsonString = serializer.serialize(o);
        
        return jsonString;
    }
    
    public Object DeserializeFromJson(String jsonString, Class retClass)
    {
        JSONDeserializer deserializer = new JSONDeserializer().use(Date.class, new DateTransformer("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        
        return deserializer.deserialize(jsonString, retClass);
    }
}
