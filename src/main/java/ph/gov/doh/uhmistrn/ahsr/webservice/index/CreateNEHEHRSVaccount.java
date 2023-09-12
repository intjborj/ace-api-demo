
package ph.gov.doh.uhmistrn.ahsr.webservice.index;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hfhudcode" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="hfhudname" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fhudaddress" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="regcode" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="provcode" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="ctymuncode" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="bgycode" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fhudtelno1" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fhudtelno2" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fhudfaxno" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fhudemail" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="headlname" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="headfname" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="headmname" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="accessKey" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "hfhudcode",
    "hfhudname",
    "fhudaddress",
    "regcode",
    "provcode",
    "ctymuncode",
    "bgycode",
    "fhudtelno1",
    "fhudtelno2",
    "fhudfaxno",
    "fhudemail",
    "headlname",
    "headfname",
    "headmname",
    "accessKey"
})
@XmlRootElement(name = "createNEHEHRSVaccount")
public class CreateNEHEHRSVaccount {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String hfhudname;
    @XmlElement(required = true, nillable = true)
    protected String fhudaddress;
    @XmlElement(required = true, nillable = true)
    protected String regcode;
    @XmlElement(required = true, nillable = true)
    protected String provcode;
    @XmlElement(required = true, nillable = true)
    protected String ctymuncode;
    @XmlElement(required = true, nillable = true)
    protected String bgycode;
    @XmlElement(required = true, nillable = true)
    protected String fhudtelno1;
    @XmlElement(required = true, nillable = true)
    protected String fhudtelno2;
    @XmlElement(required = true, nillable = true)
    protected String fhudfaxno;
    @XmlElement(required = true, nillable = true)
    protected String fhudemail;
    @XmlElement(required = true, nillable = true)
    protected String headlname;
    @XmlElement(required = true, nillable = true)
    protected String headfname;
    @XmlElement(required = true, nillable = true)
    protected String headmname;
    @XmlElement(required = true, nillable = true)
    protected String accessKey;

    /**
     * Gets the value of the hfhudcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHfhudcode() {
        return hfhudcode;
    }

    /**
     * Sets the value of the hfhudcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHfhudcode(String value) {
        this.hfhudcode = value;
    }

    /**
     * Gets the value of the hfhudname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHfhudname() {
        return hfhudname;
    }

    /**
     * Sets the value of the hfhudname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHfhudname(String value) {
        this.hfhudname = value;
    }

    /**
     * Gets the value of the fhudaddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFhudaddress() {
        return fhudaddress;
    }

    /**
     * Sets the value of the fhudaddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFhudaddress(String value) {
        this.fhudaddress = value;
    }

    /**
     * Gets the value of the regcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegcode() {
        return regcode;
    }

    /**
     * Sets the value of the regcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegcode(String value) {
        this.regcode = value;
    }

    /**
     * Gets the value of the provcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvcode() {
        return provcode;
    }

    /**
     * Sets the value of the provcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvcode(String value) {
        this.provcode = value;
    }

    /**
     * Gets the value of the ctymuncode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCtymuncode() {
        return ctymuncode;
    }

    /**
     * Sets the value of the ctymuncode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCtymuncode(String value) {
        this.ctymuncode = value;
    }

    /**
     * Gets the value of the bgycode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBgycode() {
        return bgycode;
    }

    /**
     * Sets the value of the bgycode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBgycode(String value) {
        this.bgycode = value;
    }

    /**
     * Gets the value of the fhudtelno1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFhudtelno1() {
        return fhudtelno1;
    }

    /**
     * Sets the value of the fhudtelno1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFhudtelno1(String value) {
        this.fhudtelno1 = value;
    }

    /**
     * Gets the value of the fhudtelno2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFhudtelno2() {
        return fhudtelno2;
    }

    /**
     * Sets the value of the fhudtelno2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFhudtelno2(String value) {
        this.fhudtelno2 = value;
    }

    /**
     * Gets the value of the fhudfaxno property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFhudfaxno() {
        return fhudfaxno;
    }

    /**
     * Sets the value of the fhudfaxno property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFhudfaxno(String value) {
        this.fhudfaxno = value;
    }

    /**
     * Gets the value of the fhudemail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFhudemail() {
        return fhudemail;
    }

    /**
     * Sets the value of the fhudemail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFhudemail(String value) {
        this.fhudemail = value;
    }

    /**
     * Gets the value of the headlname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeadlname() {
        return headlname;
    }

    /**
     * Sets the value of the headlname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeadlname(String value) {
        this.headlname = value;
    }

    /**
     * Gets the value of the headfname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeadfname() {
        return headfname;
    }

    /**
     * Sets the value of the headfname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeadfname(String value) {
        this.headfname = value;
    }

    /**
     * Gets the value of the headmname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeadmname() {
        return headmname;
    }

    /**
     * Sets the value of the headmname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeadmname(String value) {
        this.headmname = value;
    }

    /**
     * Gets the value of the accessKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Sets the value of the accessKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessKey(String value) {
        this.accessKey = value;
    }

}
