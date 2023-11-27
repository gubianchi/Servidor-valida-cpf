import java.io.*;
import java.net.Socket;

public class Cliente {
    public static final String HOST_PADRAO = "localhost"; // sujeito à mudança
    public static final int PORTA_PADRAO = 3000;

    public static void main(String[] args){
        Socket conexao = null;
        try{
            String host = Cliente.HOST_PADRAO;
            int porta = Cliente.PORTA_PADRAO;

            /*
            if (args.length > 0) host = args[0];
            if (args.length == 2) porta = Integer.parseInt(args[1]);
            */

            conexao = new Socket(host, porta);
            ObjectOutputStream transmissor = new ObjectOutputStream(conexao.getOutputStream());//microfone
            ObjectInputStream receptor = new ObjectInputStream(conexao.getInputStream());//autofalante
            Parceiro servidor = new Parceiro(conexao, receptor, transmissor);

            TratadoraDeComunicadoDeDesligamento tratadoraDecomunicadoDeDesligamento = new TratadoraDeComunicadoDeDesligamento(servidor);
            tratadoraDecomunicadoDeDesligamento.start();

            //ate aqui tudo deve acontecer no início do programa

            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            for(;;){
                System.out.println("Digite o CPF: ");
                String cpf = teclado.readLine();
                servidor.receba(new EnvioDeCpf(cpf));
                servidor.receba(new PedidoDeResultado());
                Comunicado comunicado = null;
                do{
                    comunicado = servidor.espie();
                }while(!(comunicado instanceof Resultado));

                Resultado resultado = (Resultado)servidor.envie();

                if (resultado.getResultado()) System.out.println("Cpf válido!");
                if (!resultado.getResultado()) System.out.println("Cpf inválido!");
                
                System.out.println("Testar outro CPF?");
                String continua = teclado.readLine();

                if(continua.equalsIgnoreCase("não")) break;
            }
            servidor.receba(new PedidoParaSair());
            System.exit(0);

            //tudo aqui esta adaptado para usar no CMD, é necessário mudar pala MOBILE
        }catch(Exception erro){}
    }
}
