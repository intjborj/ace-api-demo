
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
 *         &lt;element name="numhai" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="numdischarges" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="infectionrate" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="patientnumvap" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalventilatordays" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="resultvap" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="patientnumbsi" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalnumcentralline" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="resultbsi" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="patientnumuti" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalcatheterdays" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="resultuti" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="numssi" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="totalproceduresdone" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="resultssi" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="reportingyear" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "numhai",
    "numdischarges",
    "infectionrate",
    "patientnumvap",
    "totalventilatordays",
    "resultvap",
    "patientnumbsi",
    "totalnumcentralline",
    "resultbsi",
    "patientnumuti",
    "totalcatheterdays",
    "resultuti",
    "numssi",
    "totalproceduresdone",
    "resultssi",
    "reportingyear"
})
@XmlRootElement(name = "hospitalOperationsHAI")
public class HospitalOperationsHAI {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(required = true, nillable = true)
    protected String numhai;
    @XmlElement(required = true, nillable = true)
    protected String numdischarges;
    @XmlElement(required = true, nillable = true)
    protected String infectionrate;
    @XmlElement(required = true, nillable = true)
    protected String patientnumvap;
    @XmlElement(required = true, nillable = true)
    protected String totalventilatordays;
    @XmlElement(required = true, nillable = true)
    protected String resultvap;
    @XmlElement(required = true, nillable = true)
    protected String patientnumbsi;
    @XmlElement(required = true, nillable = true)
    protected String totalnumcentralline;
    @XmlElement(required = true, nillable = true)
    protected String resultbsi;
    @XmlElement(required = true, nillable = true)
    protected String patientnumuti;
    @XmlElement(required = true, nillable = true)
    protected String totalcatheterdays;
    @XmlElement(required = true, nillable = true)
    protected String resultuti;
    @XmlElement(required = true, nillable = true)
    protected String numssi;
    @XmlElement(required = true, nillable = true)
    protected String totalproceduresdone;
    @XmlElement(required = true, nillable = true)
    protected String resultssi;
    @XmlElement(required = true, nillable = true)
    protected String reportingyear;

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
     * Gets the value of the numhai property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumhai() {
        return numhai;
    }

    /**
     * Sets the value of the numhai property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumhai(String value) {
        this.numhai = value;
    }

    /**
     * Gets the value of the numdischarges property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumdischarges() {
        return numdischarges;
    }

    /**
     * Sets the value of the numdischarges property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumdischarges(String value) {
        this.numdischarges = value;
    }

    /**
     * Gets the value of the infectionrate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfectionrate() {
        return infectionrate;
    }

    /**
     * Sets the value of the infectionrate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfectionrate(String value) {
        this.infectionrate = value;
    }

    /**
     * Gets the value of the patientnumvap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatientnumvap() {
        return patientnumvap;
    }

    /**
     * Sets the value of the patientnumvap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientnumvap(String value) {
        this.patientnumvap = value;
    }

    /**
     * Gets the value of the totalventilatordays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalventilatordays() {
        return totalventilatordays;
    }

    /**
     * Sets the value of the totalventilatordays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalventilatordays(String value) {
        this.totalventilatordays = value;
    }

    /**
     * Gets the value of the resultvap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultvap() {
        return resultvap;
    }

    /**
     * Sets the value of the resultvap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultvap(String value) {
        this.resultvap = value;
    }

    /**
     * Gets the value of the patientnumbsi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatientnumbsi() {
        return patientnumbsi;
    }

    /**
     * Sets the value of the patientnumbsi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientnumbsi(String value) {
        this.patientnumbsi = value;
    }

    /**
     * Gets the value of the totalnumcentralline property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalnumcentralline() {
        return totalnumcentralline;
    }

    /**
     * Sets the value of the totalnumcentralline property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalnumcentralline(String value) {
        this.totalnumcentralline = value;
    }

    /**
     * Gets the value of the resultbsi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultbsi() {
        return resultbsi;
    }

    /**
     * Sets the value of the resultbsi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultbsi(String value) {
        this.resultbsi = value;
    }

    /**
     * Gets the value of the patientnumuti property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatientnumuti() {
        return patientnumuti;
    }

    /**
     * Sets the value of the patientnumuti property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientnumuti(String value) {
        this.patientnumuti = value;
    }

    /**
     * Gets the value of the totalcatheterdays property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalcatheterdays() {
        return totalcatheterdays;
    }

    /**
     * Sets the value of the totalcatheterdays property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalcatheterdays(String value) {
        this.totalcatheterdays = value;
    }

    /**
     * Gets the value of the resultuti property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultuti() {
        return resultuti;
    }

    /**
     * Sets the value of the resultuti property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultuti(String value) {
        this.resultuti = value;
    }

    /**
     * Gets the value of the numssi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumssi() {
        return numssi;
    }

    /**
     * Sets the value of the numssi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumssi(String value) {
        this.numssi = value;
    }

    /**
     * Gets the value of the totalproceduresdone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTotalproceduresdone() {
        return totalproceduresdone;
    }

    /**
     * Sets the value of the totalproceduresdone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTotalproceduresdone(String value) {
        this.totalproceduresdone = value;
    }

    /**
     * Gets the value of the resultssi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultssi() {
        return resultssi;
    }

    /**
     * Sets the value of the resultssi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultssi(String value) {
        this.resultssi = value;
    }

    /**
     * Gets the value of the reportingyear property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReportingyear() {
        return reportingyear;
    }

    /**
     * Sets the value of the reportingyear property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReportingyear(String value) {
        this.reportingyear = value;
    }

}
