import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Advisor implements Serializable
{
    private String ID;
    private String Name;
    Logger logger ;
    FileHandler fh;



    public Advisor(String id, String name) throws IOException

    {
        this.ID = id;
        this.Name = name;

        logger = Logger.getLogger("advisor");
        fh = new FileHandler("/Users/veedaa/Desktop/DCRS/src/Logs/advisor.log");

        logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.info("advisor " + this.getID() + " created!" );
        logger.info(this.ID);
        logger.info(this.Name);

    }
    public String getName()
    {
        return this.Name;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


}
