package models;

import org.hibernate.annotations.Type;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class FinancialTransaction extends Model {

    public enum TransactionSate {
        INITIATED,PROCESSING,CALLBACK,SUCCESS,FAILED
    }

    public TransactionSate sate = TransactionSate.INITIATED;

    public Date issuedAt;

    public Date finishedAt;

    public Long price;

    public String description;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    public String shetabCreationResult;

    @Lob
    @Type(type="org.hibernate.type.TextType")
    public String shetabCallbackResult;

    public String shetabUID;

    @ManyToOne
    public Publication item;

    FinancialTransaction(Publication publication){
        item = publication;
        issuedAt = new Date();
    }

}
