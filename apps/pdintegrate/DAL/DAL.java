package pdintegrate.DAL;

import java.util.Collection;

import pdstore.GUID;
import pdstore.PDStore;


/**
 * Contains the definition for this Data Access Layer.
 * 
 * 
 * @author dbra072
 *
 */
public class DAL {
	
	public static GUID MODEL_ID = new GUID("126e2e80bed011dfaaa4005056c00001");
	

	
	public static GUID OPERATION_TYPE = new GUID("126e2e95bed011dfaaa4005056c00001");
	public static GUID OBJECT_SCHEMA_TYPE = new GUID("126e2e96bed011dfaaa4005056c00001");
	public static GUID OBJECT_ATTRIBUTE_TYPE = new GUID("126e2e97bed011dfaaa4005056c00001");
	
	public static GUID PROVIDES = new GUID("126e2e98bed011dfaaa4005056c00001");
	
	public static void init(PDStore store) {
		
		//Creating the model
		GUID transaction = store.begin();
		store.createModel(transaction, MODEL_ID, "PDIntegratorModel");
		store.createType(transaction, MODEL_ID, DataSource.TYPE_ID, "Source");
		store.createType(transaction, MODEL_ID, Schema.TYPE_ID, "Schema");
		store.createType(transaction, MODEL_ID, Entity.TYPE_ID, "Entity");
		store.createType(transaction, MODEL_ID, Relation.TYPE_ID, "Relation");
		store.createType(transaction, MODEL_ID, Match.TYPE_ID, "Match");
		store.createType(transaction, MODEL_ID, Mapping.TYPE_ID, "Mapping");
		store.createType(transaction, MODEL_ID, ObjectSchema.TYPE_ID, "ObjectSchema");
		store.createType(transaction, MODEL_ID, OPERATION_TYPE, "Operation");
		
		store.createRelation(transaction, DataSource.TYPE_ID, DataSource.HAS_SCHEMA, Schema.TYPE_ID);
		
		store.createRelation(transaction, Schema.TYPE_ID, Schema.MATCH_LINK_ID, Match.TYPE_ID);
		store.createRelation(transaction, Schema.TYPE_ID, Schema.ENTITY_LINK_ID, Entity.TYPE_ID);
		
		store.createRelation(transaction, Entity.TYPE_ID, Entity.TYPEDEF_LINK_ID, PDStore.STRING_TYPEID);
		store.createRelation(transaction, Entity.TYPE_ID, Entity.RELATION_LINK_ID, Relation.TYPE_ID);
		
		
		store.createRelation(transaction, Relation.TYPE_ID, Relation.ENTITY_LINK_ID, Entity.TYPE_ID);
		store.createRelation(transaction, Relation.TYPE_ID, Relation.MULTIPLE_LINK_ID, PDStore.BOOLEAN_TYPEID);
		
		store.createRelation(transaction, ObjectSchema.TYPE_ID, ObjectSchema.ENTITY_LINK_ID, Entity.TYPE_ID);
		
		store.createRelation(transaction, ObjectSchema.TYPE_ID, ObjectSchema.MATCH_LINK_ID, Match.TYPE_ID);
		
		store.createRelation(transaction, Match.TYPE_ID, Match.MAPPING_LINK_ID, Mapping.TYPE_ID);
		
		store.createRelation(transaction, Mapping.TYPE_ID, Mapping.OPERATION_LINK_ID, PDStore.STRING_TYPEID);
		store.createRelation(transaction, Mapping.TYPE_ID, Mapping.OBJECT_ENTITY_LINK_ID, Entity.TYPE_ID);		
		store.createRelation(transaction, Mapping.TYPE_ID, Mapping.SOURCE_ENTITY_LINK_ID, Entity.TYPE_ID);
		
		
		store.commit(transaction);
		
	}
	
		public static Collection<Object> getAllInstancesOfType(PDStore repository, GUID type) {
			return null;
			
		}

}
