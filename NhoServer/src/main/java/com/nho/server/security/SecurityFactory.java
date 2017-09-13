package com.nho.server.security;

public class SecurityFactory {
//	private ClassLoader classLoader = this.getClass().getClassLoader();
	private AbstractSecurity security ;
	public SecurityFactory() {
	}
	public SecurityFactory(AbstractSecurity security){
		this.setSecurity(security);
	}
	public AbstractSecurity getSecurity() {
		if(this.security == null ){
			
		}
		return security;
	}
	public void setSecurity(AbstractSecurity security) {
		this.security = security;
	}
//	public <T extends AbstractSecurity> T getSecurity(String modelClass){
//		try{
//			@SuppressWarnings("unchecked")
//			Class<T> clazz = (Class<T>)this.classLoader.loadClass(modelClass);
//			T model = (T) clazz.newInstance();
//			
//			return model;
//		}catch(Exception exception){
//			throw new RuntimeException("create model security error :"+exception);
//		}
//	}
//	
//	public <T extends AbstractSecurity> T newModel(Class<T> modelClass){
//		return this.getSecurity(modelClass.getName());
//	}
//	
//	public ClassLoader getClassLoader(){
//		return classLoader;
//	}
//	public void setClassLoader(ClassLoader classLoader){
//		this.classLoader = classLoader;
//	}
	
	
}
