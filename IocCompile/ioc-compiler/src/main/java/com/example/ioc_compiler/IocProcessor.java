package com.example.ioc_compiler;

import com.example.ioc_annotation.BindView;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by zwl on 17-9-16.
 *
 *
 */

@AutoService(Processor.class)
public class IocProcessor extends AbstractProcessor {
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;

    //代理类路径和代理类信息的映射
    private Map<String, ProxyInfo> mProxyMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFileUtils = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annonationTypes = new LinkedHashSet<>();
        annonationTypes.add(BindView.class.getCanonicalName());
        return annonationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     * 基本步骤:
     * 1.收集信息
     * 2.生成代理类（本文把编译时生成的类叫代理类）
     * @param set
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        mProxyMap.clear();

        //collect infos
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        for(Element element : elements){
            if (!checkAnnotationUseValid(element)){
                return false;
            }

            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();

            ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);
            if(proxyInfo == null){
                proxyInfo = new ProxyInfo(mElementUtils, typeElement);
                mProxyMap.put(qualifiedName, proxyInfo);
            }

            BindView annonation = variableElement.getAnnotation(BindView.class);
            int id = annonation.value();
            proxyInfo.injectVariables.put(id, variableElement);
        }

        //generator java file
        for (String key : mProxyMap.keySet())
        {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try
            {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e)
            {
               System.out.print(proxyInfo.getTypeElement()+
                        "Unable to write injector for type %s: %s"+
                        proxyInfo.getTypeElement()+ e.getMessage());
            }

        }

        return true;
    }

    private boolean checkAnnotationUseValid(Element element) {
        return element.getKind().isField();
    }

}
