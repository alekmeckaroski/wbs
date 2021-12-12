package wbs;

import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDFS;

public class Main {

    public static void main(String[] args) {

        // Load my FOAF file
        String foafUri = "https://raw.githubusercontent.com/alekmeckaroski/wbs/main/foaf.ttl";
        Model model = ModelFactory.createDefaultModel();
        model.read(foafUri, "TURTLE");

        // Find all my friends.
        // Because I am not in a group with a colleagues I just used a random person I know that has a dbpedia page
        // I implemented it like this because when I ran the code with just 'listObjectsOfProperty' it returned hash like string as an object to the statement.
        // To get the URI i had to select it explicitly
        model.listObjectsOfProperty(FOAF.knows)
                .forEachRemaining(person -> {
                    model.listStatements(new SimpleSelector(person.asResource(), RDFS.seeAlso, (RDFNode) null))
                            .forEachRemaining(statement -> System.out.println(statement.getObject()));
                });

        // Create the sum model for all the friends
        Model sumModel = ModelFactory.createDefaultModel();
        model.listObjectsOfProperty(FOAF.knows)
                .forEachRemaining(person -> {
                    model.listStatements(new SimpleSelector(person.asResource(), RDFS.seeAlso, (RDFNode) null))
                            .forEachRemaining(statement -> {
                                Model tempModel = ModelFactory.createDefaultModel();
                                tempModel.add(statement);

                                sumModel.union(tempModel);
                            });
                });

        // Combine the models
        model.union(sumModel);
    }
}
