package com.icoder.couldnewsclient.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 * 因为存在很多个Fragment
 * 因此要进行向上抽取
 * 在BaseFragment的子类编写人员不需要对Fragment的声明周期进行了解
 * 因为如果被调用已经在BaseFragment中封装完成了
 * 因此编写子类的任务只需要直接写出抽象方法，也就是业务逻辑相关
 */
public abstract class BaseFragment extends Fragment{
	protected View rootView;
	protected LayoutInflater inflater;
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	public View getRootView() {
		return rootView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(inflateFragmentById(), null);
		this.inflater = inflater;
		return rootView;
	}

	/**
	 *	初始化view控件,和设置相关的监听
	 */
	abstract protected void initView();

	/**
	 * @return	fragment的id值
	 */
	abstract protected int inflateFragmentById();
	
	public View findViewById(int id){
		return rootView.findViewById(id);
	}
}