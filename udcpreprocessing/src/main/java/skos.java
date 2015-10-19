import org.semanticweb.skos.*;
import org.semanticweb.skosapibinding.SKOSManager;

import java.net.URI;

/**
 * Created by dacc on 10/16/2015.
 */
public class skos {
    public static void main(String[] args) {
         /*
        * How to load a SKOS vocabulay and print out the concepts and any assertions on these
        * The main object in the API include a SKOSManager which manages the loading, saving a dn editing of dataset,
        * The SKOSDataset object is a container for your SKOS vocabularies, each manager can have multiple dataset which
        * are accessed via a URIs. Finally there is a SKOSDataFactory object for creating and retrieving SKOSObject from your dataset.
         */

        try {

            // First create a new SKOSManager
            SKOSManager manager = new SKOSManager();

            // use the manager to load a SKOS vocabulary from a URI (either physical or on the web)

            SKOSDataset dataset = manager.loadDataset(URI.create("apitest.owl"));
            //"file:/Users/simon/ontologies/skos/apitest.owl"
            //"http://potato.cs.man.ac.uk/seanb/skos/diving-skos.rdf"

            // get all the concepts in this vocabulay and print out the URI

            for (SKOSConcept concept : dataset.getSKOSConcepts()) {

                System.out.println("Concept: " + concept.getURI());

                /*
                * SKOS entities such as Concepts, ConceptSchemes (See SKOSEntity in Javadoc for full list), are related to other
                * entities or literal values by three different types of relationships.
                * ObjectPropertyAssertions - These are relationships between two SKOS entities
                * DataPropertyAssertion - These relate entities to Literal values
                * SKOSAnnotation - These are either literal or entity annotation on a particular entity
                 */

                // print out object assertions

                System.out.println("\tObject property assertions:");
                for (SKOSObjectRelationAssertion objectAssertion : dataset.getSKOSObjectRelationAssertions(concept)) {
                    System.out.println("\t\t" + objectAssertion.getSKOSProperty().getURI().getFragment() + " " + objectAssertion.getSKOSObject().getURI().getFragment());

                }
                System.out.println("");


                // print out any data property assertions
                System.out.println("\tData property assertions:");
                for (SKOSDataRelationAssertion assertion : dataset.getSKOSDataRelationAssertions(concept)) {

                    // the object of a data assertion can be either a typed or untyped literal
                    SKOSLiteral literal = assertion.getSKOSObject();
                    String lang = "";
                    if (literal.isTyped()) {

                        SKOSTypedLiteral typedLiteral = literal.getAsSKOSTypedLiteral();
                        System.out.println("\t\t" + assertion.getSKOSProperty().getURI().getFragment() + " " + literal.getLiteral() + " Type:" + typedLiteral.getDataType().getURI() );
                    }
                    else {

                        // if it has  language
                        SKOSUntypedLiteral untypedLiteral = literal.getAsSKOSUntypedLiteral();
                        if (untypedLiteral.hasLang()) {
                            lang = untypedLiteral.getLang();
                        }
                        System.out.println("\t\t" + assertion.getSKOSProperty().getURI().getFragment() + " " + literal.getLiteral() + " Lang:" + lang);

                    }
                }
                System.out.println("");


                // finally get any OWL annotations - the object of a annotation property can be a literal or an entity
                System.out.println("\tAnnotation property assertions:");
                for (SKOSAnnotation assertion : dataset.getSKOSAnnotations(concept)) {

                    // if the annotation is a literal annotation?
                    String lang = "";
                    String value = "";

                    if (assertion.isAnnotationByConstant()) {

                        SKOSLiteral literal = assertion.getAnnotationValueAsConstant();
                        value = literal.getLiteral();
                        if (!literal.isTyped()) {
                            // if it has  language
                            SKOSUntypedLiteral untypedLiteral = literal.getAsSKOSUntypedLiteral();
                            if (untypedLiteral.hasLang()) {
                                lang = untypedLiteral.getLang();
                            }
                        }
                    }
                    else {
                        // annotation is some resource
                        SKOSEntity entity = assertion.getAnnotationValue();
                        value = entity.getURI().getFragment();
                    }
                    System.out.println("\t\t" + assertion.getURI().getFragment() + " " + value + " Lang:" + lang);
                }
                System.out.println("");
            }
        } catch (SKOSCreationException e) {
            e.printStackTrace();
        }
    }
}
