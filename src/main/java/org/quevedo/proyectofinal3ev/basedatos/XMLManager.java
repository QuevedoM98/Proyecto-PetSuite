package org.quevedo.proyectofinal3ev.basedatos;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XMLManager {
    public static <T> boolean writeXML(T objeto, String fileName){
        boolean result = false;

        try{
            //Paso 1:Crear el contexto  de JaxB para la clase que queremos serializar
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());

            //Paso 2: Proceso Marshalling: convertir objeto en XML
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(objeto,new File(fileName));
            result = true;
        }catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static <T> T readXML (T objeto, String fileName){
        T objetos = null;
        try{
            //Paso 1:Crear el contexto  de JaxB para la clase que queremos serializar
            JAXBContext context = JAXBContext.newInstance(objeto.getClass());
            //Paso 2: Unmarshalling: leer el XML y convertirlo a un objeto
            Unmarshaller unmarshaller = context.createUnmarshaller();
            objetos = (T) unmarshaller.unmarshal(new File(fileName));

        }catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return objetos;
    }

}
