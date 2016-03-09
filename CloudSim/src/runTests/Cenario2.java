/**
 * Este é o nosso segundo arquivo de simulação.
 * Aqui iremos simular um cenário de 4 hosts, 4 VM e 4 Cloudlets.
 * Métrica a ser utiliada: Modelagem da taxa de chegada de clientes.
 */
package runTests;

/** Pacotes nativos do Java. */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/** Pacotes do CloudSim. */
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
import sun.misc.VM;

/**
 * Classe principal da simulação.
 * @author Iago Silva
 * @author Leylane Ferreira
 */
public class Cenario2 {
    /** Lista de Cloudlets. */
    private static List<Cloudlet> cloudletList;
    
    /** Lista de VMs. */
    private static List<Vm> vmlist;

    public static void main(String[] args) {
        try{
            int num_user = 1;
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = true;

            CloudSim.init(num_user, calendar, trace_flag);

            @SuppressWarnings("unused")
            Datacenter datacenter = createDatacenter("Datacenter_0"); //criando DataCenter

            DatacenterBroker broker1 = new DatacenterBroker("Broker"+1); //criando broker
            int userId = broker1.getId(); //setando a id do broker criado
            
            vmlist = new ArrayList<>();
            
            Vm vm1 = new Vm(0, userId, 500, 1, 512, 100, 10000, "Xen", new CloudletSchedulerTimeShared()); //criando VM
            Vm vm2 = new Vm(1, userId, 500, 1, 512, 100, 10000, "Xen", new CloudletSchedulerTimeShared()); //criando VM
            Vm vm3 = new Vm(2, userId, 500, 1, 512, 100, 10000, "Xen", new CloudletSchedulerTimeShared()); //criando VM
            Vm vm4 = new Vm(3, userId, 500, 1, 512, 100, 10000, "Xen", new CloudletSchedulerTimeShared()); //criando VM
            
            vmlist.add(vm1); //adicionando-o na lista de VMs
            vmlist.add(vm2); //adicionando-o na lista de VMs
            vmlist.add(vm3); //adicionando-o na lista de VMs
            vmlist.add(vm4); //adicionando-o na listae de VMs
            
            broker1.submitVmList(vmlist); //passando lista1 de VMs pro broker
            
            cloudletList = new ArrayList<>(); //instanciando cloudlet
            
            UtilizationModel utilizationModel = new UtilizationModelFull();
            
            Cloudlet cloudlet1 = new Cloudlet(0, 40000, 1, 3000, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(userId);
            Cloudlet cloudlet2 = new Cloudlet(1, 800000000, 1, 100, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet2.setUserId(userId);
            Cloudlet cloudlet3 = new Cloudlet(2, 400000, 1, 150, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet3.setUserId(userId);
            Cloudlet cloudlet4 = new Cloudlet(3, 50000, 1, 800, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet4.setUserId(userId);
            
            cloudletList.add(cloudlet1);
            cloudletList.add(cloudlet2);
            cloudletList.add(cloudlet3);
            cloudletList.add(cloudlet4);
            
            broker1.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation(); //inicia simulação
            List<Cloudlet> newList1 = broker1.getCloudletReceivedList(); //pega resultado da simulação
            CloudSim.stopSimulation(); //finaliza simulação
            
            Log.print("=============> Usuário "+userId+"    ");
            printCloudletList(newList1);
            
            Log.printLine("Simulação finalizada!");
                    
        } catch (Exception e){
            
        }
        
    }
    
    private static Datacenter createDatacenter(String name){

	List<Host> hostList = new ArrayList<>(); //lista de hosts
        
        //segue as caracteristicas de cada maquina... :
        List<Pe> peList = new ArrayList<>(); //lista de CPUs/Cores
        int mips=1000; //taxa de MIPS, em milhões de instruções por segundo
        peList.add(new Pe(0, new PeProvisionerSimple(mips))); //criando uma CPU, parâmetros:(idDoCore, mipsDenifido)
        peList.add(new Pe(1, new PeProvisionerSimple(mips))); // essa nossa maquina tera dois cores (dual core)
        int hostId=0; //id do host
	int ram = 4096; //memória RAM
	long storage = 1000000; //espaço em disço
	int bw = 10000; //largura de banda
        
        hostList.add(new Host(
                                hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList))); //  máquina criada
         
        List<Pe> peList2 = new ArrayList<>(); //lista de CPUs/Cores
        peList2.add(new Pe(0, new PeProvisionerSimple(mips)));// add um core param: (idDoCore, quantidade de execução de instruções por segundo (em milhões))
        peList2.add(new Pe(1, new PeProvisionerSimple(mips))); //add o segundo core...
        peList2.add(new Pe(2, new PeProvisionerSimple(mips))); //add o terceiro core... pronto, essa máquina terá três cores (nucleos de processamento)
        
        hostId++;
        hostList.add(new Host(
                                hostId,
    				new RamProvisionerSimple(2048),
    				new BwProvisionerSimple(1000),
    				1000000,
    				peList2,
    				new VmSchedulerSpaceShared(peList2))); //  segunda máquina criada... e aí?agora sim
        
        List<Pe> peList3 = new ArrayList<>();
        peList3.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList3.add(new Pe(1, new PeProvisionerSimple(mips)));
        
        hostId++;
        hostList.add(new Host(
                                hostId,
    				new RamProvisionerSimple(1024),
    				new BwProvisionerSimple(1000),
    				1000000,
    				peList3,
    				new VmSchedulerSpaceShared(peList3)));//  terceira máquina criada... e aí?agora sim
        
        List<Pe> peList4 = new ArrayList<>();
        peList4.add(new Pe(0,new PeProvisionerSimple(mips)));
        peList4.add(new Pe(1,new PeProvisionerSimple(mips)));
        peList4.add(new Pe(2,new PeProvisionerSimple(mips)));
        peList4.add(new Pe(3,new PeProvisionerSimple(mips)));
        
        hostId++;
        hostList.add(new Host(
                                hostId,
    				new RamProvisionerSimple(4096),
    				new BwProvisionerSimple(1000),
    				1000000,
    				peList4,
    				new VmSchedulerSpaceShared(peList4)));//  terceira máquina criada... e aí?agora sim
        
        
        
        //Todas as características referentes ao Datacenter
        String arch = "x86"; //arquitetura do SO
	String os = "Windows"; //SO
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
