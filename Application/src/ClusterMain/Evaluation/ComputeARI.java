package ClusterMain.Evaluation;


public class ComputeARI
{
    /*
    public double evaluate(Map<Integer, Integer> ClusteredDara, Map<Integer, Integer> GroundTruth)
    {
        //int[][] aij = new int[ClusteredDara.size()][GroundTruth.size()];
        int H_Sum = 0;

        for(Map.Entry<Integer, Integer> _ClusteredData : ClusteredDara.entrySet())
        {
            for(Map.Entry<Integer, Integer> _GroundTruth: GroundTruth.entrySet())
            {
                if(_GroundTruth.getKey() == _ClusteredData.getKey())
                {
                    H_Sum++;
                }
            }
        }
        return 0;


        /*
        NOT
        Working
        //Create the contingency table for computing index
        double[] truthSums = new double[GroundTruth.size()];
        double[] clusterSums = new double[ClusteredDara.size()];

        double[][] table = new double[clusterSums.length][truthSums.length];
        double n = 0.0;



        double sumAllTable = 0.0;
        double addCTerm = 0.0, addLTerm = 0.0;

        for(int i = 0; i < table.length; i++)
        {
            double a_i = clusterSums[i];
            addCTerm += a_i*(a_i-1)/2;

            for(int j = 0; j < table[i].length; j++)
            {
                if(i == 0)
                {
                    double b_j = truthSums[j];
                    addLTerm += b_j*(b_j-1)/2;
                }

                double n_ij = table[i][j];
                double n_ij_c2 = n_ij*(n_ij-1)/2;
                sumAllTable += n_ij_c2;
            }
        }

        double longMultTerm = exp(log(addCTerm)+log(addLTerm)-(log(n)+log(n-1)-log(2)));//numericaly more stable verison
        return 1.0-(sumAllTable-longMultTerm)/(addCTerm/2+addLTerm/2-longMultTerm);*/
}


