package satoshidoge.walletutil;

/**
 *
 * @author Devin
 */
public class WalletFactory {
    public DogeCoindAccess getDogeCoind(){
        return new DogeCoind();
    }
}
