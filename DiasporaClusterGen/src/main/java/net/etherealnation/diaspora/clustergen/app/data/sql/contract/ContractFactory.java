package net.etherealnation.diaspora.clustergen.app.data.sql.contract;

public class ContractFactory {
    private static ClusterContractHelper clusterContract;
    private static SystemContractHelper systemContract;
    private static SystemAspectContractHelper systemAspectContract;

    public static ContractHelper getHelper(final String contract) {
        if (contract.equals(ClusterGenContract.CLUSTER_TABLE)) {
            if (clusterContract == null) {
                clusterContract = new ClusterContractHelper();
            }

            return clusterContract;
        }

        if (contract.equals(ClusterGenContract.SYSTEM_TABLE)) {
            if (systemContract == null) {
                systemContract = new SystemContractHelper();
            }

            return systemContract;
        }

        if (contract.equals(ClusterGenContract.SYSTEM_ASPECT_TABLE)) {
            if (systemAspectContract == null) {
                systemAspectContract = new SystemAspectContractHelper();
            }

            return systemAspectContract;
        }
        return null;
    }

    public static ContractHelper[] getAllHelpers() {
        return new ContractHelper[]{
                getHelper(ClusterGenContract.SYSTEM_TABLE),
                getHelper(ClusterGenContract.CLUSTER_TABLE),
                getHelper(ClusterGenContract.SYSTEM_ASPECT_TABLE)
        };
    }
}
