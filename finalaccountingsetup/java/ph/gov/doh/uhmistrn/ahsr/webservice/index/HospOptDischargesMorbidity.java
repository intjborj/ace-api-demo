
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
 *         &lt;element name="icd10desc" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="munder1" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="funder1" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m1to4" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f1to4" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m5to9" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f5to9" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m10to14" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f10to14" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m15to19" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f15to19" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m20to24" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f20to24" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m25to29" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f25to29" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m30to34" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f30to34" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m35to39" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f35to39" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m40to44" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f40to44" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m45to49" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f45to49" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m50to54" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f50to54" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m55to59" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f55to59" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m60to64" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f60to64" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m65to69" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f65to69" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="m70over" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="f70over" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="msubtotal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="fsubtotal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="grandtotal" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="icd10code" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
 *         &lt;element name="icd10category" type="{http://www.w3.org/2001/XMLSchema}string" form="unqualified"/>
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
    "icd10Desc",
    "munder1",
    "funder1",
    "m1To4",
    "f1To4",
    "m5To9",
    "f5To9",
    "m10To14",
    "f10To14",
    "m15To19",
    "f15To19",
    "m20To24",
    "f20To24",
    "m25To29",
    "f25To29",
    "m30To34",
    "f30To34",
    "m35To39",
    "f35To39",
    "m40To44",
    "f40To44",
    "m45To49",
    "f45To49",
    "m50To54",
    "f50To54",
    "m55To59",
    "f55To59",
    "m60To64",
    "f60To64",
    "m65To69",
    "f65To69",
    "m70Over",
    "f70Over",
    "msubtotal",
    "fsubtotal",
    "grandtotal",
    "icd10Code",
    "icd10Category",
    "reportingyear"
})
@XmlRootElement(name = "hospOptDischargesMorbidity")
public class HospOptDischargesMorbidity {

    @XmlElement(required = true, nillable = true)
    protected String hfhudcode;
    @XmlElement(name = "icd10desc", required = true, nillable = true)
    protected String icd10Desc;
    @XmlElement(required = true, nillable = true)
    protected String munder1;
    @XmlElement(required = true, nillable = true)
    protected String funder1;
    @XmlElement(name = "m1to4", required = true, nillable = true)
    protected String m1To4;
    @XmlElement(name = "f1to4", required = true, nillable = true)
    protected String f1To4;
    @XmlElement(name = "m5to9", required = true, nillable = true)
    protected String m5To9;
    @XmlElement(name = "f5to9", required = true, nillable = true)
    protected String f5To9;
    @XmlElement(name = "m10to14", required = true, nillable = true)
    protected String m10To14;
    @XmlElement(name = "f10to14", required = true, nillable = true)
    protected String f10To14;
    @XmlElement(name = "m15to19", required = true, nillable = true)
    protected String m15To19;
    @XmlElement(name = "f15to19", required = true, nillable = true)
    protected String f15To19;
    @XmlElement(name = "m20to24", required = true, nillable = true)
    protected String m20To24;
    @XmlElement(name = "f20to24", required = true, nillable = true)
    protected String f20To24;
    @XmlElement(name = "m25to29", required = true, nillable = true)
    protected String m25To29;
    @XmlElement(name = "f25to29", required = true, nillable = true)
    protected String f25To29;
    @XmlElement(name = "m30to34", required = true, nillable = true)
    protected String m30To34;
    @XmlElement(name = "f30to34", required = true, nillable = true)
    protected String f30To34;
    @XmlElement(name = "m35to39", required = true, nillable = true)
    protected String m35To39;
    @XmlElement(name = "f35to39", required = true, nillable = true)
    protected String f35To39;
    @XmlElement(name = "m40to44", required = true, nillable = true)
    protected String m40To44;
    @XmlElement(name = "f40to44", required = true, nillable = true)
    protected String f40To44;
    @XmlElement(name = "m45to49", required = true, nillable = true)
    protected String m45To49;
    @XmlElement(name = "f45to49", required = true, nillable = true)
    protected String f45To49;
    @XmlElement(name = "m50to54", required = true, nillable = true)
    protected String m50To54;
    @XmlElement(name = "f50to54", required = true, nillable = true)
    protected String f50To54;
    @XmlElement(name = "m55to59", required = true, nillable = true)
    protected String m55To59;
    @XmlElement(name = "f55to59", required = true, nillable = true)
    protected String f55To59;
    @XmlElement(name = "m60to64", required = true, nillable = true)
    protected String m60To64;
    @XmlElement(name = "f60to64", required = true, nillable = true)
    protected String f60To64;
    @XmlElement(name = "m65to69", required = true, nillable = true)
    protected String m65To69;
    @XmlElement(name = "f65to69", required = true, nillable = true)
    protected String f65To69;
    @XmlElement(name = "m70over", required = true, nillable = true)
    protected String m70Over;
    @XmlElement(name = "f70over", required = true, nillable = true)
    protected String f70Over;
    @XmlElement(required = true, nillable = true)
    protected String msubtotal;
    @XmlElement(required = true, nillable = true)
    protected String fsubtotal;
    @XmlElement(required = true, nillable = true)
    protected String grandtotal;
    @XmlElement(name = "icd10code", required = true, nillable = true)
    protected String icd10Code;
    @XmlElement(name = "icd10category", required = true, nillable = true)
    protected String icd10Category;
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
     * Gets the value of the icd10Desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcd10Desc() {
        return icd10Desc;
    }

    /**
     * Sets the value of the icd10Desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcd10Desc(String value) {
        this.icd10Desc = value;
    }

    /**
     * Gets the value of the munder1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMunder1() {
        return munder1;
    }

    /**
     * Sets the value of the munder1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMunder1(String value) {
        this.munder1 = value;
    }

    /**
     * Gets the value of the funder1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFunder1() {
        return funder1;
    }

    /**
     * Sets the value of the funder1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunder1(String value) {
        this.funder1 = value;
    }

    /**
     * Gets the value of the m1To4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM1To4() {
        return m1To4;
    }

    /**
     * Sets the value of the m1To4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM1To4(String value) {
        this.m1To4 = value;
    }

    /**
     * Gets the value of the f1To4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF1To4() {
        return f1To4;
    }

    /**
     * Sets the value of the f1To4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF1To4(String value) {
        this.f1To4 = value;
    }

    /**
     * Gets the value of the m5To9 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM5To9() {
        return m5To9;
    }

    /**
     * Sets the value of the m5To9 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM5To9(String value) {
        this.m5To9 = value;
    }

    /**
     * Gets the value of the f5To9 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF5To9() {
        return f5To9;
    }

    /**
     * Sets the value of the f5To9 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF5To9(String value) {
        this.f5To9 = value;
    }

    /**
     * Gets the value of the m10To14 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM10To14() {
        return m10To14;
    }

    /**
     * Sets the value of the m10To14 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM10To14(String value) {
        this.m10To14 = value;
    }

    /**
     * Gets the value of the f10To14 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF10To14() {
        return f10To14;
    }

    /**
     * Sets the value of the f10To14 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF10To14(String value) {
        this.f10To14 = value;
    }

    /**
     * Gets the value of the m15To19 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM15To19() {
        return m15To19;
    }

    /**
     * Sets the value of the m15To19 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM15To19(String value) {
        this.m15To19 = value;
    }

    /**
     * Gets the value of the f15To19 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF15To19() {
        return f15To19;
    }

    /**
     * Sets the value of the f15To19 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF15To19(String value) {
        this.f15To19 = value;
    }

    /**
     * Gets the value of the m20To24 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM20To24() {
        return m20To24;
    }

    /**
     * Sets the value of the m20To24 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM20To24(String value) {
        this.m20To24 = value;
    }

    /**
     * Gets the value of the f20To24 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF20To24() {
        return f20To24;
    }

    /**
     * Sets the value of the f20To24 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF20To24(String value) {
        this.f20To24 = value;
    }

    /**
     * Gets the value of the m25To29 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM25To29() {
        return m25To29;
    }

    /**
     * Sets the value of the m25To29 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM25To29(String value) {
        this.m25To29 = value;
    }

    /**
     * Gets the value of the f25To29 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF25To29() {
        return f25To29;
    }

    /**
     * Sets the value of the f25To29 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF25To29(String value) {
        this.f25To29 = value;
    }

    /**
     * Gets the value of the m30To34 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM30To34() {
        return m30To34;
    }

    /**
     * Sets the value of the m30To34 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM30To34(String value) {
        this.m30To34 = value;
    }

    /**
     * Gets the value of the f30To34 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF30To34() {
        return f30To34;
    }

    /**
     * Sets the value of the f30To34 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF30To34(String value) {
        this.f30To34 = value;
    }

    /**
     * Gets the value of the m35To39 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM35To39() {
        return m35To39;
    }

    /**
     * Sets the value of the m35To39 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM35To39(String value) {
        this.m35To39 = value;
    }

    /**
     * Gets the value of the f35To39 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF35To39() {
        return f35To39;
    }

    /**
     * Sets the value of the f35To39 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF35To39(String value) {
        this.f35To39 = value;
    }

    /**
     * Gets the value of the m40To44 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM40To44() {
        return m40To44;
    }

    /**
     * Sets the value of the m40To44 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM40To44(String value) {
        this.m40To44 = value;
    }

    /**
     * Gets the value of the f40To44 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF40To44() {
        return f40To44;
    }

    /**
     * Sets the value of the f40To44 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF40To44(String value) {
        this.f40To44 = value;
    }

    /**
     * Gets the value of the m45To49 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM45To49() {
        return m45To49;
    }

    /**
     * Sets the value of the m45To49 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM45To49(String value) {
        this.m45To49 = value;
    }

    /**
     * Gets the value of the f45To49 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF45To49() {
        return f45To49;
    }

    /**
     * Sets the value of the f45To49 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF45To49(String value) {
        this.f45To49 = value;
    }

    /**
     * Gets the value of the m50To54 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM50To54() {
        return m50To54;
    }

    /**
     * Sets the value of the m50To54 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM50To54(String value) {
        this.m50To54 = value;
    }

    /**
     * Gets the value of the f50To54 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF50To54() {
        return f50To54;
    }

    /**
     * Sets the value of the f50To54 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF50To54(String value) {
        this.f50To54 = value;
    }

    /**
     * Gets the value of the m55To59 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM55To59() {
        return m55To59;
    }

    /**
     * Sets the value of the m55To59 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM55To59(String value) {
        this.m55To59 = value;
    }

    /**
     * Gets the value of the f55To59 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF55To59() {
        return f55To59;
    }

    /**
     * Sets the value of the f55To59 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF55To59(String value) {
        this.f55To59 = value;
    }

    /**
     * Gets the value of the m60To64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM60To64() {
        return m60To64;
    }

    /**
     * Sets the value of the m60To64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM60To64(String value) {
        this.m60To64 = value;
    }

    /**
     * Gets the value of the f60To64 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF60To64() {
        return f60To64;
    }

    /**
     * Sets the value of the f60To64 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF60To64(String value) {
        this.f60To64 = value;
    }

    /**
     * Gets the value of the m65To69 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM65To69() {
        return m65To69;
    }

    /**
     * Sets the value of the m65To69 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM65To69(String value) {
        this.m65To69 = value;
    }

    /**
     * Gets the value of the f65To69 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF65To69() {
        return f65To69;
    }

    /**
     * Sets the value of the f65To69 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF65To69(String value) {
        this.f65To69 = value;
    }

    /**
     * Gets the value of the m70Over property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getM70Over() {
        return m70Over;
    }

    /**
     * Sets the value of the m70Over property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setM70Over(String value) {
        this.m70Over = value;
    }

    /**
     * Gets the value of the f70Over property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getF70Over() {
        return f70Over;
    }

    /**
     * Sets the value of the f70Over property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setF70Over(String value) {
        this.f70Over = value;
    }

    /**
     * Gets the value of the msubtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsubtotal() {
        return msubtotal;
    }

    /**
     * Sets the value of the msubtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsubtotal(String value) {
        this.msubtotal = value;
    }

    /**
     * Gets the value of the fsubtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFsubtotal() {
        return fsubtotal;
    }

    /**
     * Sets the value of the fsubtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFsubtotal(String value) {
        this.fsubtotal = value;
    }

    /**
     * Gets the value of the grandtotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrandtotal() {
        return grandtotal;
    }

    /**
     * Sets the value of the grandtotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrandtotal(String value) {
        this.grandtotal = value;
    }

    /**
     * Gets the value of the icd10Code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcd10Code() {
        return icd10Code;
    }

    /**
     * Sets the value of the icd10Code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcd10Code(String value) {
        this.icd10Code = value;
    }

    /**
     * Gets the value of the icd10Category property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcd10Category() {
        return icd10Category;
    }

    /**
     * Sets the value of the icd10Category property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcd10Category(String value) {
        this.icd10Category = value;
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
