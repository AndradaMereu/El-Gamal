import java.util.ArrayList;
import java.util.Random;

public class ElGamal
{
    private int private_key;
    private ArrayList<Integer> encrypted_message = new ArrayList<>();

    public int getPrivate_key() {
        return private_key;
    }

    public ArrayList<Integer> getEncrypted_message() {
        return encrypted_message;
    }

    /**
     * Method for generating the private key
     */
    public void generateKey(int q)
    {
        Random random = new Random();
        private_key = random.nextInt(q);
    }

    public ArrayList<Integer>writeAsPowers(int a)
    {
        int aux;
        ArrayList<Integer> powers  = new ArrayList<>();
        //repeatedly divides the number by 2 and stores the remainders into a vector, thus writing the number as powers of 2
        while (a != 0)
        {
            aux = a % 2;
            powers.add(aux);
            a = a / 2;
        }
        return powers;
    }

    //computes a^(b) (mod c), using the modular exponentiation method
    /**
     *
     * @param a the base
     * @param b the exponent
     * @param c the value for which we compute the modulo operation
     * @return the value of a^b mod c
     */
    int repeatedSquaring(int a, int b, int c)
    {
        int aux;
        ArrayList<Integer> powers = new ArrayList<>();

        ArrayList<Integer> d = writeAsPowers(b);

        aux = a;
        if (aux >= c) powers.add(aux%c);
        else powers.add(aux);

        //use modular exponentiation repeated squaring method to compute a^2,a^2^2...
        for (int i =1; i < d.size(); i++)
        {
            aux = (aux%c) * (aux%c);
            if (aux >= b) aux = aux % c;
            powers.add(aux);
        }
        //compute a^(b) mod c
        int res = 1;
        for (int i = 0; i < d.size(); i++)
        {
            if (d.get(i) == 1) res = res * powers.get(i);
            if (res >= c) res = res % c;
        }
        return res;
    }

    /**
     * Encrypting the message
     * the function takes as parameters the plaintext message, q=the random prime number,
     * h=g^a mod p, g = the generator
     * the function returns g^k mod q, where k is a random number
     */
    public int encrypt(String message, int q, int h, int g)
    {
        Random rand = new Random();
        int r = rand.nextInt(q);

        int s = repeatedSquaring(h,r, q);
        int p  = repeatedSquaring(g, r, q);

        for (int i = 0; i < message.length(); i++)
        {
            encrypted_message.add(s * message.charAt(i));
        }

        return p;
    }


    /**
    the function decrypts the encrypted message
    IN: p=the number returned by the encryption function, q=the random prime number
    OUT: the decrypted message
     */
    public String decrypt(int p, int q)
    {
        String msg = "";
        int h = repeatedSquaring(p, private_key, q);
        for (Integer integer : encrypted_message) {
            int index = integer / h;
            char c = (char) (index);
            msg = msg.concat(String.valueOf(c));
        }
        return msg;
    }

    public int verifyIsGenerator(int value)
    {
        int generator = 0;
        boolean ok = true;
        if (value <= 2)
            return 0;
        while (ok)
        {
            Random random = new Random();
            generator = random.nextInt(value);
            int i = 1;
            while (i < value)
                if (Math.pow(generator, value / i) == 1)
                    break;
                else
                    i++;
             if (i == value)
                 ok = false;
        }
        return generator;
    }

    public boolean isPrime(int nr)
    {
        if(nr<=3 || nr%2==0)
            return nr == 2 || nr == 3;
        int div = 3;
        while(div <= Math.sqrt(nr) && nr % div != 0)
            div += 2;
        return nr % div != 0;
    }

    public static void main(String[] args) {

        ElGamal gama = new ElGamal();

        String msg = "cool cat hoola hoop";
        System.out.println("The plaintext is: "+ msg);

        Random rand = new Random();
        int random_number = rand.nextInt(10000);
        while(!(gama.isPrime(random_number)))
            random_number = rand.nextInt(10000);
        System.out.println("random number: " + random_number);
        int generator = gama.verifyIsGenerator(random_number);
        gama.generateKey(random_number);

        int h = gama.repeatedSquaring(generator, gama.getPrivate_key(), random_number);

        System.out.println("g used: "+ generator);
        System.out.println("g^a used: "+ h);

        int p = gama.encrypt(msg, random_number, h, generator);

        System.out.println("Encrypted text: " + gama.getEncrypted_message());
        String decrypted  = gama.decrypt(p, random_number);
        System.out.println("decrypted message: " + decrypted);
    }


}
