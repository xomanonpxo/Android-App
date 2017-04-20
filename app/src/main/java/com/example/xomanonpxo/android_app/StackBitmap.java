package com.example.xomanonpxo.android_app;

import android.accounts.AccountsException;
import android.graphics.Bitmap;

/**
 * Created by xomanonpxo on 20/04/17.
 */

public class StackBitmap {

    //Données membres d'objet
    private static int _size = 2;
    private int _top;
    private Bitmap[] _stack;

    //Constructeur
    public StackBitmap(Bitmap[] stack){
        while(stack.length > _size)
            _size *= 2;
        _stack = new Bitmap[_size];
        for(int i = 0; i < stack.length; ++i)
            _stack[i] = stack[i];
        _top = stack.length - 1;
    }

    //Accesseurs
    public Bitmap getTop(){
        return _stack[_top];
    }

    public void setTop(Bitmap bmp){
        pop();
        push(bmp);
    }

    public int getMaxSize(){
        return _size;
    }

    //Méthodes de la forme canonique
    public StackBitmap clone(){
        Bitmap[] tmp = new Bitmap[_top+1];
        for(int i = 0; i < tmp.length; ++i)
            tmp[i] = _stack[i].copy(_stack[i].getConfig(), true);
        return new StackBitmap(tmp);
    }

    public String toString(){
        if(_top < 0)
            return "Stack is empty !\n";
        StringBuffer str = new StringBuffer("Stack : ");
        str.append(_top+1);
        str.append(" Bitmaps.\n");
        return str.toString();
    }

    public boolean equals(StackBitmap s){
        if(_size != s._size || _top != s._top)
            return false;
        for(int i = 0; i <= _top; ++i){
            if(_stack[i] != s._stack[i])
                return false;
        }
        return true;
    }

    //Méthodes
    public void push(Bitmap bmp){
        resizeArray();
        _top += 1;
        _stack[_top] = bmp;
    }

    public void pop(){
        if(_top < 0)
            throw new ArrayIndexOutOfBoundsException();
        _stack[_top] = null;
        _top -= 1;
    }

    public boolean isEmpty(){
        return _top < 0;
    }

    private void resizeArray(){
        if(_top + 1 == _size)
            _size *= 2;
        Bitmap[] tmp = new Bitmap[_size];
        for(int i = 0; i < _stack.length; ++i)
            tmp[i] = _stack[i];
        _stack = tmp;
    }
}
