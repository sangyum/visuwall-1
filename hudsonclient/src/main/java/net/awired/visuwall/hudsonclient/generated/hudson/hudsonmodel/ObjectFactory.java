//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.21 at 12:25:24 PM CET 
//


package net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.jsmadja.wall.projectwall.generated.hudson.hudsonmodel package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Hudson_QNAME = new QName("", "Hudson");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.jsmadja.wall.projectwall.generated.hudson.hudsonmodel
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HudsonScmRepositoryBrowser }
     * 
     */
    public HudsonScmRepositoryBrowser createHudsonScmRepositoryBrowser() {
        return new HudsonScmRepositoryBrowser();
    }

    /**
     * Create an instance of {@link HudsonModelHealthReport }
     * 
     */
    public HudsonModelHealthReport createHudsonModelHealthReport() {
        return new HudsonModelHealthReport();
    }

    /**
     * Create an instance of {@link HudsonModelAbstractItem }
     * 
     */
    public HudsonModelAbstractItem createHudsonModelAbstractItem() {
        return new HudsonModelAbstractItem();
    }

    /**
     * Create an instance of {@link HudsonModelJobProperty }
     * 
     */
    public HudsonModelJobProperty createHudsonModelJobProperty() {
        return new HudsonModelJobProperty();
    }

    /**
     * Create an instance of {@link HudsonModelJob }
     * 
     */
    public HudsonModelJob createHudsonModelJob() {
        return new HudsonModelJob();
    }

    /**
     * Create an instance of {@link HudsonModelView }
     * 
     */
    public HudsonModelView createHudsonModelView() {
        return new HudsonModelView();
    }

    /**
     * Create an instance of {@link HudsonModelAbstractProject }
     * 
     */
    public HudsonModelAbstractProject createHudsonModelAbstractProject() {
        return new HudsonModelAbstractProject();
    }

    /**
     * Create an instance of {@link HudsonModelHudson }
     * 
     */
    public HudsonModelHudson createHudsonModelHudson() {
        return new HudsonModelHudson();
    }

    /**
     * Create an instance of {@link HudsonModelTimeSeries }
     * 
     */
    public HudsonModelTimeSeries createHudsonModelTimeSeries() {
        return new HudsonModelTimeSeries();
    }

    /**
     * Create an instance of {@link HudsonModelNode }
     * 
     */
    public HudsonModelNode createHudsonModelNode() {
        return new HudsonModelNode();
    }

    /**
     * Create an instance of {@link HudsonScmSCM }
     * 
     */
    public HudsonScmSCM createHudsonScmSCM() {
        return new HudsonScmSCM();
    }

    /**
     * Create an instance of {@link HudsonModelOverallLoadStatistics }
     * 
     */
    public HudsonModelOverallLoadStatistics createHudsonModelOverallLoadStatistics() {
        return new HudsonModelOverallLoadStatistics();
    }

    /**
     * Create an instance of {@link HudsonModelLoadStatistics }
     * 
     */
    public HudsonModelLoadStatistics createHudsonModelLoadStatistics() {
        return new HudsonModelLoadStatistics();
    }

    /**
     * Create an instance of {@link HudsonModelMultiStageTimeSeries }
     * 
     */
    public HudsonModelMultiStageTimeSeries createHudsonModelMultiStageTimeSeries() {
        return new HudsonModelMultiStageTimeSeries();
    }

    /**
     * Create an instance of {@link HudsonModelLabelsLabelAtom }
     * 
     */
    public HudsonModelLabelsLabelAtom createHudsonModelLabelsLabelAtom() {
        return new HudsonModelLabelsLabelAtom();
    }

    /**
     * Create an instance of {@link HudsonModelLabel }
     * 
     */
    public HudsonModelLabel createHudsonModelLabel() {
        return new HudsonModelLabel();
    }

    /**
     * Create an instance of {@link HudsonModelRun }
     * 
     */
    public HudsonModelRun createHudsonModelRun() {
        return new HudsonModelRun();
    }

    /**
     * Create an instance of {@link HudsonModelActionable }
     * 
     */
    public HudsonModelActionable createHudsonModelActionable() {
        return new HudsonModelActionable();
    }

    /**
     * Create an instance of {@link HudsonModelQueueItem }
     * 
     */
    public HudsonModelQueueItem createHudsonModelQueueItem() {
        return new HudsonModelQueueItem();
    }

    /**
     * Create an instance of {@link HudsonModelLabelsLabelAtomProperty }
     * 
     */
    public HudsonModelLabelsLabelAtomProperty createHudsonModelLabelsLabelAtomProperty() {
        return new HudsonModelLabelsLabelAtomProperty();
    }

    /**
     * Create an instance of {@link HudsonModelRunArtifact }
     * 
     */
    public HudsonModelRunArtifact createHudsonModelRunArtifact() {
        return new HudsonModelRunArtifact();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HudsonModelHudson }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "Hudson")
    public JAXBElement<HudsonModelHudson> createHudson(HudsonModelHudson value) {
        return new JAXBElement<HudsonModelHudson>(_Hudson_QNAME, HudsonModelHudson.class, null, value);
    }

}