/**
 * Este é o nosso primeiro arquivo de simulação.
 * Aqui iremos simular um cenário de 4 hosts, 1 VM e 1 Cloudlet.
 * Métrica a ser utiliada: Modelagem da taxa de chegada de clientes.
 */
package runTests;

/** Imports nativos do Java. */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/** Imports do CloudSim. */
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * Classe principal da simulação.
 * @author Iago Silva
 * @author Leylane Ferreira
 */
public class Cenario1 {

    /** Lista de Cloudlets. */
    private static List<Cloudlet> cloudletList1;

    /** Lista de VMs. */
    private static List<Vm> vmlist1;

    public static void main(String[] args) {
        
        Log.printLine("Iniciando cenário 1...");
        
        try {
            int num_user = 2; //quantidade de usuários da nuvem
            Calendar calendar = Calendar.getInstance(); //algo para o tempo (?)
            boolean trace_flag = false; //algo de rastreamento (??)
            
            CloudSim.init(num_user, calendar, trace_flag); //antes de se iniciar qualquer entidade, deve-se iniciar a biblioteca do CloudSim
            
            @SuppressWarnings("unused")
            Datacenter datacenter0 = createDatacenter("Datacenter_0"); //criando DataCenter
            
            
            DatacenterBroker broker1 = new DatacenterBroker("Broker"+1); //criando broker
            int brokerId = broker1.getId(); //setando a id do broker criado
            
            vmlist1 = new ArrayList<>();
            
            //Características da VM
            int vmid = 0; //id da VM
            int mips = 250; //milhões de instruções por segundo
            long size = 10000; //image size (MB)
            int ram = 512; //memória da máquina virtual
            long bw = 1000; //(???)
            int pesNumber = 1; //Número de CPUs
            String vmm = "Xen"; //Nome
            
            Vm vm1 = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared()); //criando VM
            vmlist1.add(vm1); //adicionando-o na lista de VMs
            
            broker1.submitVmList(vmlist1); //passando lista1 de VMs pro broker
            
            cloudletList1 = new ArrayList<>(); //instanciando cloudlet
            
            //propriedades do cloudlet
            int id = 0;
            long length = 40000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();
            
            //criando o broker e setando sua id
            Cloudlet cloudlet1 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(brokerId);
            
            cloudletList1.add(cloudlet1); //adicionando Cloudlet na lista
            
            broker1.submitCloudletList(cloudletList1); //submetendo lista de cloudlets para o broker
            
            CloudSim.startSimulation(); //inicia simulação
            List<Cloudlet> newList1 = broker1.getCloudletReceivedList(); //pega resultado da simulação
            CloudSim.stopSimulation(); //finaliza simulação
            
            Log.print("=============> Usuário "+brokerId+"    ");
            printCloudletList(newList1);
            
            Log.printLine("Simulação finalizada!");
        } catch (Exception e){
            e.printStackTrace();
            Log.printLine("A simulação foi interrompida por causa desta exceção acima.");
        }    
    }
    
    /**
     * Esse método cria um DataCenter, é copiado dos exemplos que já vem
     * no próprio CloudSim.
     * @param name Nome do DataCenter.
     * @return Retorna um DataCenter.
     */
    private static Datacenter createDatacenter(String name){

	List<Host> hostList = new ArrayList<>(); //lista de hosts
        List<Pe> peList = new ArrayList<>(); //lista de CPUs/Cores

        int mips=1000; //taxa de MIPS, em milhões de instruções por segundo
        peList.add(new Pe(0, new PeProvisionerSimple(mips))); //criando uma CPU, parâmetros:(idDoCore, mipsDenifido)

	//definição de atributos do host
        //int hostId=0; //id do host
	int ram = 1024; //memória RAM
	long storage = 1000000; //espaço em disço
	int bw = 10000; //largura de banda

        for (int idHost = 0; idHost < 4; idHost++) {
            hostList.add(new Host(
                                idHost,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList))); //  máquina criada
        }
	//criando o host, com as características definidas anteriormente
	
        //Todas as características referentes ao Datacenter
        String arch = "x86"; //arquitetura do SO
	String os = "Linux"; //SO
	String vmm = "Xen"; //VM
	double time_zone = 10.0; //fuso horário
	double cost = 3.0; //custo para o uso do processamento deste recurso
	double costPerMem = 0.05; //custo por uso de memória deste recurso
	double costPerStorage = 0.001; //custo por armazenamento neste recurso
	double costPerBw = 0.0; //custo por uso de bw neste recurso (???)
	LinkedList<Storage> storageList = new LinkedList<>(); //(????)

	DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw); //setando características do DataCenter

        //criação do objeto de DataCenter de retorno
        Datacenter datacenter = null;
	try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
	} catch (Exception e) {
            e.printStackTrace();
	}
        
        return datacenter;
    }
    
    /**
     * Mostra na tela os resultados obtidos na simulação.
     * @param list Resultados da simulação em uma Cloudlet.
     */
    private static void printCloudletList(List<Cloudlet> list) {
		
        int size = list.size();
	Cloudlet cloudlet;

	String indent = "    ";
	Log.printLine();
	Log.printLine("========== OUTPUT ==========");
	Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

	DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
                Log.print("SUCCESS");

                Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
            }
        }
    }
}
    
