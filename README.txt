This is the PDStore source project.
The development environment used is Eclipse


Documentation:

Most recent documentation is contained in JavaDoc comments in the various files.
Important starting points are:
- PDCoreI and PDStoreI, the fundamental interfaces of PDStore
- pdstore.PDStore, the convenience class that one will mostly instanciate to use PDStore.
- PDSimpleWorkingCopy and PDWorkingCopy, the Interface and ConvenienceClass to work with
  the Data Access Layer.

Other important accessways to understanding PDStore are:
- The sample applications in the "apps" folder, in particular:
  - The diagram editor, which exemplifies the fundamental tool architecture that all PDStore
    based tools should use.
  - 
- The views of PDStore. These are "applets" have become so central that they are moved to
  the core pdstore package under pdstore.ui.
  Some of them can be used from sample code in the "examples" source folders
  The current views are:
  - The treeview, it can be started from:
        /pdstore/examples/book/ExampleTreeViewer.java
  - The Historyview, visualizing transactions and branches in PDStore
  - The Graphview to edit graphs of Instances in PDStore.
  
- The testcases in the source folder "test". 
  In particular the SPARQL Testcases show one of the most advances uses of PDStore as a database:
  /pdstore/test/pdstore/sparql/AllSparqlTests.java
  
There is also a collection of short tutorials and documentations in the "Documentation" folder.

  