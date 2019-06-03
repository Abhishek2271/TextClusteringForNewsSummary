package ClusterMain;
import de.lmu.ifi.dbs.elki.algorithm.clustering.DBSCAN;
import de.lmu.ifi.dbs.elki.algorithm.clustering.gdbscan.*;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.DoubleVector;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.StaticArrayDatabase;
import de.lmu.ifi.dbs.elki.datasource.ArrayAdapterDatabaseConnection;
import de.lmu.ifi.dbs.elki.datasource.DatabaseConnection;
import de.lmu.ifi.dbs.elki.utilities.*;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

import java.util.List;

public class EkliDBScanClusterer
{
    /*
    Database db = makeSimpleDatabase("3clusters-and-noise-2d.csv", 330);
    Clustering<Model> result = new ELKIBuilder<DBSCAN<DoubleVector>>(DBSCAN.class) //
            .with(DBSCAN.Parameterizer.EPSILON_ID, 0.04) //-
            .with(DBSCAN.Parameterizer.MINPTS_ID, 20) //
            .build().run(db);

    String[] data = new String[1];

    //populate data according to your app
    DatabaseConnection dbc = new ArrayAdapterDatabaseConnection(data);
    Database db = new StaticArrayDatabase(dbc, null);


    /*
    //dbscan algorithm setup
    List<ListParameterization> parameters = new ListParameterization();
    params.addParameter(DBSCAN.Parameterizer.EPSILON_ID, 0.04);
    params.addParameter(DBSCAN.Parameterizer.MINPTS_ID, 20);
    DBSCAN<DoubleVector> dbscan = ClassGenericsUtil.parameterizeOrAbort(DBSCAN.class, DBSCAN.Parameterizer.EPSILON_ID, 0.04, );

    //run DBSCAN on database
    Clustering<Model> result = dbscan.run(db);
    */

}
