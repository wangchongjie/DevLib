package com.baidu.unbiz.devlib.joiner;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.slf4j.Logger;

import com.baidu.unbiz.devlib.common.bo.MethodPair;
import com.baidu.unbiz.devlib.common.exception.DevLibException;
import com.baidu.unbiz.devlib.common.log.AopLogFactory;
import com.baidu.unbiz.devlib.cache.AtomicComputeCache;
import com.baidu.unbiz.devlib.clazz.ClassUtils;
import com.baidu.unbiz.devlib.crypto.Fs64Utils;

/**
 * 代码生成类
 * 
 * @author wangchongjie
 */
public class JoinerCodeGenerator {

    protected static final Logger LOG = AopLogFactory.getLogger(JoinerCodeGenerator.class);

    /**
     * Joiner缓存
     */
    private static AtomicComputeCache<String, ItemJoinerSupport> atomicComputeJoinerCache =
            new AtomicComputeCache<String, ItemJoinerSupport>();

    /**
     * 按指定class的key和value获取OlapJoinerSupport
     * 
     * @param mergeKeys
     * @param mergeVals
     * @param clazz
     * @return ItemJoinerSupport
     */
    public static ItemJoinerSupport getOlapJoinerSupportClass(final Set<String> mergeKeys, final Set<String> mergeVals,
            final Class<?> clazz) {

        String keyInCache = clazz.getName() + mergeKeys + mergeVals;
        ItemJoinerSupport result = atomicComputeJoinerCache.preGetAlreadyDoneResult(keyInCache);
        if (result != null) {
            return result;
        }
        result = atomicComputeJoinerCache.getComputeResult(keyInCache, new Callable<ItemJoinerSupport>() {
            @Override
            public ItemJoinerSupport call() {
                return JoinerCodeGenerator.generateJoinerClassCode(mergeKeys, mergeVals, clazz);
            }
        });
        return result;
    }

    /**
     * 生成SetValuesMethod
     * 
     * @param mergeVals
     * @param clazz
     * @return SetValuesMethod4JoinerClassCode
     * @since 2015-7-28 by wangchongjie
     */
    private static String generateSetValuesMethod4JoinerClassCode(Set<String> mergeVals, Class<?> clazz) {
        String clazzName = clazz.getName();
        String setValuesMethod =
                "public void setValues(Object source, Object target){" 
                        + clazzName + " s = (" + clazzName + ") source;"
                        + clazzName + " t = (" + clazzName + ") target;";

        Map<String, MethodPair> methodMap = ClassUtils.getGStterMethodMapper(clazz);
        for (String val : mergeVals) {
            MethodPair methods = methodMap.get(val);
            methods = (methods == null) ? methodMap.get((val = val.toLowerCase())) : methods;
            Method getter = methods.getter;
            String getterName = getter.getName();
            Method setter = methods.setter;
            String setterName = setter.getName();
            setValuesMethod += "t." + setterName + "(" + "s." + getterName + "()" + ");";
        }
        setValuesMethod += "}";
        return setValuesMethod;
    }

    /**
     * 生成GetKeysMethod
     * 
     * @param mergeKeys
     * @param clazz
     * @return GetKeysMethod4JoinerClassCode
     */
    private static String generateGetKeysMethod4JoinerClassCode(Set<String> mergeKeys, Class<?> clazz) {
        String clazzName = clazz.getName();
        String getKeysMethod =
                "public String getKeys(Object t){" + clazzName + " item = (" + clazzName + ") t;"
                        + "StringBuilder multiKey = new StringBuilder(\"\");";

        Map<String, MethodPair> methodMap = ClassUtils.getGStterMethodMapper(clazz);
        for (String key : mergeKeys) {
            MethodPair methods = methodMap.get(key);
            Method getter = methods.getter;
            String getterName = getter.getName();
            getKeysMethod += "multiKey.append(\"-\");multiKey.append(item." + getterName + "());";
        }
        getKeysMethod += "return multiKey.toString();}";
        return getKeysMethod;
    }

    private static AtomicBoolean alreadyInitClassPath = new AtomicBoolean(false);

    /**
     * 生成OlapJoinerSupport
     * 
     * @param mergeKeys
     * @param mergeVals
     * @param clazz
     * @return ItemJoinerSupport
     * @since 2015-7-28 by wangchongjie
     */
    private static ItemJoinerSupport generateJoinerClassCode(Set<String> mergeKeys, Set<String> mergeVals,
            Class<?> clazz) {

        String helperName = generateJoinerHelperClassName(mergeKeys, mergeVals, clazz);
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(helperName);
        if (!alreadyInitClassPath.get()) {
            pool.appendClassPath(new ClassClassPath(clazz));
            alreadyInitClassPath.set(true);
        }

        String getKeysMethod = generateGetKeysMethod4JoinerClassCode(mergeKeys, clazz);
        // System.out.println("-----" + getKeysMethod);
        LOG.info("JoinerCodeGenerator--getKeysMethod body:" + getKeysMethod);

        String setValuesMethod = generateSetValuesMethod4JoinerClassCode(mergeVals, clazz);
        // System.out.println("-----"+setValuesMethod);
        LOG.info("JoinerCodeGenerator--setValuesMethod body:" + setValuesMethod);

        ItemJoinerSupport joiner = null;
        String errMsg = "generateJoinerClassCode fail: ";
        try {
            ctClass.addInterface(pool.get(ItemJoinerSupport.class.getName()));
            CtMethod newMethod = CtNewMethod.make(getKeysMethod, ctClass);
            ctClass.addMethod(newMethod);

            newMethod = CtNewMethod.make(setValuesMethod, ctClass);
            ctClass.addMethod(newMethod);

            CtConstructor cons = new CtConstructor(new CtClass[] {}, ctClass);
            cons.setBody("{System.out.println(\"class code init---" + helperName + "\");}");
            ctClass.addConstructor(cons);
            // ctClass.writeFile();
            // ctClass.defrost();
            Class<?> joinerClazz = ctClass.toClass();
            joiner = (ItemJoinerSupport) joinerClazz.newInstance();
            ctClass.detach();

        } catch (CannotCompileException e) {
            LOG.error(errMsg, e);
            throw new DevLibException(e);
        } catch (InstantiationException e) {
            LOG.error(errMsg, e);
            throw new DevLibException(e);
        } catch (IllegalAccessException e) {
            LOG.error(errMsg, e);
            throw new DevLibException(e);
        } catch (NotFoundException e) {
            LOG.error(errMsg, e);
            throw new DevLibException(e);
        }

        return joiner;
    }

    /**
     * 生成JoinerHelper的类名称
     * 
     * @param mergeKeys
     * @param mergeVals
     * @param clazz
     * @return JoinerHelper的类名称
     * @since 2015-7-28 by wangchongjie
     */
    private static String generateJoinerHelperClassName(Set<String> mergeKeys, Set<String> mergeVals, Class<?> clazz) {
        String clazzName = clazz.getName();
        String paramSign = Fs64Utils.signFs64("" + mergeKeys + mergeVals).toString();
        String helperSuffix = "JoinerSupport_";
        String helperName = clazzName + helperSuffix + paramSign;
        return helperName;
    }
    
}
