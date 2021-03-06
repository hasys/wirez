package org.wirez.bpmn.backend.marshall.json.parser;

import org.wirez.bpmn.backend.marshall.json.parser.common.ArrayParser;
import org.wirez.bpmn.backend.marshall.json.parser.common.ObjectParser;
import org.wirez.bpmn.backend.marshall.json.parser.common.StringFieldParser;
import org.wirez.core.graph.Edge;
import org.wirez.core.graph.Node;
import org.wirez.core.graph.content.view.View;
import org.wirez.core.graph.content.view.ViewConnector;

import java.util.LinkedList;
import java.util.List;

public class NodeParser extends ElementParser<Node<View, Edge>> {
    
    private final List<Parser> children = new LinkedList<>();
    
    public NodeParser(String name, Node<View, Edge> element) {
        super(name, element);
    }
    
    public NodeParser addChild( Parser parser ) {
        children.add( parser );
        return this;
    }

    @Override
    public void initialize(Context context) {
        super.initialize(context);

        // Children.
        ArrayParser childrenParser = new ArrayParser( "childShapes" );
        for (Parser childParser : children) {
            if ( childParser instanceof ContextualParser ) {
                ( (ContextualParser) childParser).initialize(context);
                childrenParser.addParser( childParser );
            }
        }
        super.addParser( childrenParser );
        
        // Outgoing.
        ArrayParser outgoingParser = new ArrayParser( "outgoing" );
        super.addParser( outgoingParser );
        List<Edge> outEdges = element.getOutEdges();
        if ( null != outEdges && !outEdges.isEmpty() ) {
            for ( Edge edge : outEdges ) {
                // Only add the edges with view connector types into the resulting structure to generate the bpmn definition.
                if ( edge.getContent() instanceof ViewConnector) {
                    String edgeId = edge.getUUID();
                    outgoingParser.addParser( new ObjectParser( "" ).addParser( new StringFieldParser( "resourceId", edgeId ) ) );
                }
            }
        }
        
    }

    
}
