package cn.hutool.core.lang.func;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;

/**
 * Lambda相关工具类
 *
 * @author looly, Scen
 * @since 5.6.3
 */
public class LambdaUtil {

	private static final SimpleCache<String, SerializedLambda> cache = new SimpleCache<>();

	/**
	 * 解析lambda表达式,加了缓存。
	 * 该缓存可能会在任意不定的时间被清除
	 *
	 * @param <T>  Lambda类型
	 * @param func 需要解析的 lambda 对象（无参方法）
	 * @return 返回解析后的结果
	 */
	public static <T> SerializedLambda resolve(Func1<T, ?> func) {
		return _resolve(func);
	}

	/**
	 * 解析lambda表达式,加了缓存。
	 * 该缓存可能会在任意不定的时间被清除
	 *
	 * @param <R>  Lambda返回类型
	 * @param func 需要解析的 lambda 对象（无参方法）
	 * @return 返回解析后的结果
	 * @since 5.7.23
	 */
	public static <R> SerializedLambda resolve(Func0<R> func) {
		return _resolve(func);
	}

	/**
	 * 获取lambda表达式函数（方法）名称
	 *
	 * @param <P>  Lambda参数类型
	 * @param func 函数（无参方法）
	 * @return 函数名称
	 */
	public static <P> String getMethodName(Func1<P, ?> func) {
		return resolve(func).getImplMethodName();
	}

	public static <P> Class<P> getImplClass(Func1<P, ?> func) {
		return ClassUtil.loadClass(resolve(func).getImplClass().replace(CharPool.SLASH, CharPool.DOT));
	}

	/**
	 * 获取lambda表达式函数（方法）名称
	 *
	 * @param <R>  Lambda返回类型
	 * @param func 函数（无参方法）
	 * @return 函数名称
	 * @since 5.7.23
	 */
	public static <R> String getMethodName(Func0<R> func) {
		return resolve(func).getImplMethodName();
	}

	public static <R> Class<R> getImplClass(Func0<?> func) {
		return ClassUtil.loadClass(resolve(func).getImplClass().replace(CharPool.SLASH, CharPool.DOT));
	}

	/**
	 * 获取lambda表达式Getter或Setter函数（方法）对应的字段名称，规则如下：
	 * <ul>
	 *     <li>getXxxx获取为xxxx，如getName得到name。</li>
	 *     <li>setXxxx获取为xxxx，如setName得到name。</li>
	 *     <li>isXxxx获取为xxxx，如isName得到name。</li>
	 *     <li>其它不满足规则的方法名抛出{@link IllegalArgumentException}</li>
	 * </ul>
	 *
	 * @param <T>  Lambda类型
	 * @param func 函数（无参方法）
	 * @return 方法名称
	 * @throws IllegalArgumentException 非Getter或Setter方法
	 * @since 5.7.10
	 */
	public static <T> String getFieldName(Func1<T, ?> func) throws IllegalArgumentException {
		return BeanUtil.getFieldName(getMethodName(func));
	}

	/**
	 * 获取lambda表达式Getter或Setter函数（方法）对应的字段名称，规则如下：
	 * <ul>
	 *     <li>getXxxx获取为xxxx，如getName得到name。</li>
	 *     <li>setXxxx获取为xxxx，如setName得到name。</li>
	 *     <li>isXxxx获取为xxxx，如isName得到name。</li>
	 *     <li>其它不满足规则的方法名抛出{@link IllegalArgumentException}</li>
	 * </ul>
	 *
	 * @param <T>  Lambda类型
	 * @param func 函数（无参方法）
	 * @return 方法名称
	 * @throws IllegalArgumentException 非Getter或Setter方法
	 * @since 5.7.23
	 */
	public static <T> String getFieldName(Func0<T> func) throws IllegalArgumentException {
		return BeanUtil.getFieldName(getMethodName(func));
	}

	/**
	 * 解析lambda表达式,加了缓存。
	 * 该缓存可能会在任意不定的时间被清除
	 *
	 * @param func 需要解析的 lambda 对象
	 * @return 返回解析后的结果
	 */
	private static SerializedLambda _resolve(Serializable func) {
		return cache.get(func.getClass().getName(), () -> ReflectUtil.invoke(func, "writeReplace"));
	}
}
