/**
 *
 * @author Devin Wiley
 */

package asltree;

public class BinarySearchTree<AnyType extends Comparable<? super AnyType>> {
    private static final int ALLOWED_IMBALANCE = 1;
    private static class BinaryNode<AnyType> {
        BinaryNode( AnyType theElement){
            this( theElement, null, null);
        }
        BinaryNode( AnyType theElement, BinarySearchTree.BinaryNode<AnyType> lt,
                BinarySearchTree.BinaryNode<AnyType> rt){
            element = theElement;
            if(lt != null){
                this.left = lt;
            } else if(element != null){
                this.left = new BinarySearchTree.BinaryNode<>(null, null, null);
                this.left.right = new BinarySearchTree.BinaryNode<>(null, null, this);
                
            }
            if(rt != null){
                this.right = rt;
            }
            height = 0;
        }
        
        AnyType element;
        BinarySearchTree.BinaryNode<AnyType> left;
        BinarySearchTree.BinaryNode<AnyType> right;
        int height;
    }
    private BinarySearchTree.BinaryNode<AnyType> root;
    public BinarySearchTree(){
        makeEmpty();
    }
    public void makeEmpty(){
        root = new BinarySearchTree.BinaryNode<>(null, null, null);
    }
    public boolean isEmpty(){
        return root == null;
    }
    public boolean contains(AnyType x){
        return contains(x, root);
    }
    public AnyType findMin(){
        return findMin(root).element;
    }
    public AnyType findMax(){
        return findMax(root).element;
    }
    public void insert(AnyType x){
        root = insert(x, root);
        //root = balance(root);
    }
    public void remove(AnyType x){
        root = remove(x, root);
    }
    
    private boolean contains(AnyType x, BinarySearchTree.BinaryNode<AnyType> t){
        if(t == null || isEmpty()){
            return false;
        }
        while(t.element != null){
            int compareResult = 0;
            try{
                compareResult = x.compareTo(t.element);
            } catch (ClassCastException e){ }
            
            if (compareResult < 0){
                t = t.left;
            } else if (compareResult > 0){
                t = t.left.right;
            } else {
                return true;
            }
        }
        return false;
    }
    public BinarySearchTree.BinaryNode<AnyType> findMin(BinarySearchTree.BinaryNode<AnyType> t){
        boolean found = false;
        if(t != null){
            while (t.left.element != null && !found){
                t = t.left;
            }
        }
        return t;
    }
    public BinarySearchTree.BinaryNode<AnyType> findMax(BinarySearchTree.BinaryNode<AnyType> t){
        if(t != null){
            while (t.left.right.element != null){
                t = t.left.right;
            }
        }
        return t;
    }
    public void printPreorder(){
        if (isEmpty()){
            System.out.println("Empty tree");
        } else {
            printPreorder(root);
        }
    }
    
    private void printPreorder(BinaryNode<AnyType> t){
        if (t.element != null){
            System.out.println("(" + t.element + ", " + height(t) + ")");
            printPreorder(t.left);
            printPreorder(t.left.right);
        }
    }
    
    private int height(BinaryNode<AnyType> t){
        if (t.element == null){
            return t.height = -1;
        } else {
            return t.height = Math.max(t.left == null ? -1 : height(t.left),
                    t.left.right == null ? -1 : height(t.left.right)) + 1;
        }
    }
    
    
    private BinarySearchTree.BinaryNode<AnyType> find(AnyType x, BinarySearchTree.BinaryNode<AnyType> t){
        if(!contains(x, t)){
            return null;
        }
        while(t.element != null){
            int compareResult = 0;
            try{
                compareResult = x.compareTo(t.element);
            } catch (ClassCastException e){
            }
            
            if (compareResult < 0){
                t = t.left;
            } else if (compareResult > 0){
                t = t.left.right;
            } else {
                break;
            }
        }
        return t;
    }
    private BinarySearchTree.BinaryNode<AnyType> insert(AnyType x, BinarySearchTree.BinaryNode<AnyType> t){
        if(t.element == null){
            return new BinarySearchTree.BinaryNode<>(x, null, null);
        }
        BinarySearchTree.BinaryNode<AnyType> temp = t;
        boolean insert = false;
        while(!insert){
            int compareResult = x.compareTo(t.element);
            if( compareResult < 0){
                if(t.left.element == null){
                    if(t.left.right.element != null){
                        t.left = new BinarySearchTree.BinaryNode<>(x, null, t.left.right);
                        //t = balance(t);
                    } else {
                        t.left = new BinarySearchTree.BinaryNode<>(x, null, t.left.right);
                        //t = balance(t);
                    }
                    insert = true;
                } else {
                    t = t.left;
                }
            } else if(compareResult > 0){
                if(t.left.right.element == null){
                    t.left.right = new BinarySearchTree.BinaryNode<>(x, null, t);
                    //t = balance(t);
                    insert = true;
                } else {
                    t = t.left.right;
                }
            } else if(compareResult == 0){
                insert = true;
            }
        }
        return balance(temp);
    }
    
    private BinaryNode<AnyType> balance(BinaryNode<AnyType> t){
        if (t.element == null){
            return t;
        }
        t.left = balance(t.left);
        t.left.right = balance(t.left.right);
        if (height(t.left) - height(t.left.right) > ALLOWED_IMBALANCE){
            if (height(t.left.left) >= height(t.left.left.right)){
                t = rotateWithLeftChild(t);
            } else {
                t = doubleWithLeftChild(t);
            }
        } else if (height(t.left.right) - height(t.left) > ALLOWED_IMBALANCE){
            if (height(t.left.right.left.right) >= height(t.left.right.left)){
                t = rotateWithRightChild(t);
            } else {
                t = doubleWithRightChild(t);
            }
        }
        t.height = Math.max(height(t.left), height(t.left.right)) + 1;
        return t;
    }
    
    private BinaryNode<AnyType> rotateWithLeftChild(BinaryNode<AnyType> k2){
        BinaryNode<AnyType> k1 = k2.left;
        k2.left = k1.left.right;
        k2.left.right = k1.right;
        k2.right = k1;
        k1.left.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.left.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }
    private BinaryNode<AnyType> doubleWithLeftChild(BinaryNode<AnyType> k3){
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }
    private BinaryNode<AnyType> rotateWithRightChild(BinaryNode<AnyType> k1){
        BinaryNode<AnyType> k2 = k1.left.right;
        k1.left.right = k2.left;
        k2.right = k1.right;
        k1.right = k2.left.right;
        k1.left.right.right = k1;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k1.left.right)) + 1;
        k2.height = Math.max(height(k2.left.right), k1.height) + 1;
        return k2;
    }
    private BinaryNode<AnyType> doubleWithRightChild(BinaryNode<AnyType> k3){
        k3.left.right = rotateWithLeftChild(k3.left.right);
        return rotateWithRightChild(k3);
    }
    
    private BinarySearchTree.BinaryNode<AnyType> remove(AnyType x, BinarySearchTree.BinaryNode<AnyType> t){
        BinarySearchTree.BinaryNode<AnyType> temp = t;
        BinarySearchTree.BinaryNode<AnyType> minMax = null;
        if(!contains(x, t)){
            System.out.println(x + " is not in the tree.");
            return t;
        }
        t = find(x, t);
        AnyType replacement = null;
        BinarySearchTree.BinaryNode<AnyType> delete = null;
        if(t.left.right.element != null){
            replacement = findMin(t.left.right).element;
            if(findMin(t.left.right).left.right.element != null && findMin(t.left.right) != t.left.right){
                minMax = findMin(t.left.right).left.right;
                minMax.right = findMin(t.left.right).right;
                findMin(t.left.right).right.right.left = minMax;
            } else  if (findMin(t.left.right).left.right.element != null){
                minMax = findMin(t.left.right).left.right;
                minMax.right = findMin(t.left.right).right;
                findMin(t.left.right).right.left.right = minMax;
            } else {
                delete = findMin(t.left.right);
                delete.left.right = null;
                delete.left = null;
                delete.element = null;
                delete.height = 0;
                delete = null;
            }
            t.element = replacement;
        } else if (t.left.element != null){
            replacement = findMax(t.left).element;
            if (findMax(t.left).left.element != null && findMax(t.left) != t.left){
                minMax = findMax(t.left).left;
                minMax.right = findMax(t.left).right;
                findMax(t.left).right.left.right = minMax;
            } else if (findMax(t.left).left.element != null){
                minMax = findMax(t.left).left;
                minMax.right = findMax(t.left).right;
                findMax(t.left).right.right.left = minMax;
            } else {
                delete = findMax(t.left);
                delete.left.right = null;
                delete.left = null;
                delete.element = null;
                delete.height = 0;
                delete = null;
            }
            t.element = replacement;
        } else {
            t.element = null;
            t.left.right = null;
            t.left = null;
            t.height = 0;
        }
        return balance(temp);
    }
}
