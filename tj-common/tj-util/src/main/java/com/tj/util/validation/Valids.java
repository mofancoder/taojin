package com.tj.util.validation;

import org.springframework.util.DigestUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 校验工具类 Created by yelo on 2015/10/20.
 */
public class Valids {

    public static final String PHONE_REGEX = "^(0|86|17951)?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|19[0-9]|12[0-9]|16[0-9]|14[57])[0-9]{8}$";
    private static final String EMAIL_REGEX = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    private Object param;
    private Class<?>[] clazzs;
    private Map<String, String> errors = new HashMap<String, String>();
    private Class<?>[] classType = new Class<?>[]{
            AssertFalse.class, AssertTrue.class, Email.class, Length.class,
            Max.class, Min.class, Mobile.class, NotBlank.class, NotEmpty.class,
            NotNull.class, Null.class, Pattern.class, Range.class, Size.class
    };

    private Valids(Object param) {
        this.param = param;
        this.clazzs = new Class<?>[0];
        verify();
    }

    private Valids(Class<?>[] clazzs) {
        this.clazzs = clazzs;
    }

    public static Valids of(Class<?>... clazzs) {
        return new Valids(checkNotNull(clazzs));
    }

    public static Valids verify(Object param) {
        return new Valids(checkNotNull(param));
    }

    /**
     * 手机号判断
     *
     * @param str
     * @return
     */
    public static boolean isPhone(String str) {
        return isBlank(str) ? false : str.matches(PHONE_REGEX);
    }

    /**
     * 空判断
     *
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (isEmpty(str)) {
            return true;
        }
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 空判断
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 非空判断
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 非空判断
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 邮箱判断
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        return isBlank(str) ? false : str.matches(EMAIL_REGEX);
    }

    /**
     * 判断字符串数组是否有空字符串
     *
     * @param arrs
     * @return
     */
    public static boolean hasEmpty(String... arrs) {
        if (arrs == null || arrs.length == 0) {
            return true;
        }
        for (String str : arrs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * md5加密
     *
     * @param param 明文密码或者是明文md5之后的结果
     * @return
     */
    public static String MD5Hex(String param) {
        if (32 == param.length()) {//这是明文md5之后的结果
            return genEncryptPwdByMd5pwd(param);
        } else {
            String md5Pwd = DigestUtils.md5DigestAsHex(param.getBytes());//先md5
            return genEncryptPwdByMd5pwd(md5Pwd);
        }
    }

    /**
     * @param md5Pwd: 明文md5之后的字符串
     */
    public static String genEncryptPwdByMd5pwd(String md5Pwd) {
        return new StringBuilder()
                .append(md5Pwd).reverse()
                .delete(2, 4).delete(8, 11).delete(3, 9).toString()
                .toUpperCase();
    }

    /**
     * 获取MD%5 sign
     *
     * @param sign
     * @return
     */
    public static String MD5Sign(String sign) {
        if (sign == null)
            return sign;
        return DigestUtils.md5DigestAsHex(sign.getBytes());
    }

    public static boolean contains(Object[] arrs, Object... target) {
        if (arrs == null || arrs.length == 0) {
            return false;
        }
        for (Object o : target) {
            for (Object obj : arrs) {
                if (o == obj || obj.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isEmpty(Object[] arrs) {
        return arrs == null || arrs.length == 0;
    }

    private static Set<Class<?>> solveClass(Class<?>... classes) {
        if (isEmpty(classes)) {
            return Collections.emptySet();
        }
        Set<Class<?>> result = new HashSet<Class<?>>();
        for (Class clazz : classes) {
            result.add(clazz);
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class c : interfaces) {
                result.add(c);
            }
        }
        return result;
    }

    private static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("参数不能为空！");
        }
        return obj;
    }

    public static void main(String[] args) throws Exception {


    }

    public Valids check(Object param) {
        this.param = param;
        verify();
        return this;
    }

    public Map<String, String> result() {
        if (!errors.isEmpty()) {
            errors.put("code", "47");
            errors.put("msg", "failure");
        }
        return errors;
    }

    public boolean isOk() {
        return errors.isEmpty();
    }

    private Map<String, String> verify() {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            for (Field field : fields) {
                Annotation[] anns = field.getAnnotations();

                field.setAccessible(true);
                Object value = field.get(param);
                for (Annotation ann : anns) {
                    Class<? extends Annotation> clazz = ann.annotationType();
                    if (!isValid(clazz)) {
                        continue;
                    }
                    Method scope = clazz.getMethod("scope");
                    Class<?>[] cls = (Class<?>[]) scope.invoke(ann);
                    if (isEmpty(clazzs) || contains(solveClass(clazzs).toArray(), solveClass(cls).toArray())) {
                        if (AssertTrue.class.equals(clazz)) {
                            assertTrue(new Model(field, value), (AssertTrue) ann);
                        } else if (AssertFalse.class.equals(clazz)) {
                            assertFalse(new Model(field, value), (AssertFalse) ann);
                        } else if (Email.class.equals(clazz)) {
                            email(new Model(field, value), (Email) ann);
                        } else if (Length.class.equals(clazz)) {
                            length(new Model(field, value), (Length) ann);
                        } else if (Max.class.equals(clazz)) {
                            max(new Model(field, value), (Max) ann);
                        } else if (Min.class.equals(clazz)) {
                            min(new Model(field, value), (Min) ann);
                        } else if (Mobile.class.equals(clazz)) {
                            mobile(new Model(field, value), (Mobile) ann);
                        } else if (NotBlank.class.equals(clazz)) {
                            notBlank(new Model(field, value), (NotBlank) ann);
                        } else if (NotEmpty.class.equals(clazz)) {
                            notEmpty(new Model(field, value), (NotEmpty) ann);
                        } else if (NotNull.class.equals(clazz)) {
                            notNull(new Model(field, value), (NotNull) ann);
                        } else if (Null.class.equals(clazz)) {
                            isNull(new Model(field, value), (Null) ann);
                        } else if (Pattern.class.equals(clazz)) {
                            pattern(new Model(field, value), (Pattern) ann);
                        } else if (Range.class.equals(clazz)) {
                            range(new Model(field, value), (Range) ann);
                        } else if (Size.class.equals(clazz)) {
                            size(new Model(field, value), (Size) ann);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errors;
    }

    private boolean isValid(Class<?> clazz) {
        return contains(classType, clazz);
    }

    private void size(Model model, Size ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof Collection)
                && !(model.getValue() instanceof Map)
                && !model.getField().getType().isArray()) {
            errors.put(model.getName(), "该字段必须为集合或数组");
            return;
        }
        if (model.getValue() instanceof Collection) {
            Collection<?> coll = (Collection<?>) model.getValue();
            if (coll.size() > ann.max() || coll.size() < ann.min()) {
                errors.put(model.getName(),
                        String.format(ann.msg(), ann.min(), ann.max()));
            }
        } else if (model.getField().getType().isArray()) {
            int len = Array.getLength(model.getValue());
            if (len > ann.max() || len < ann.min()) {
                errors.put(model.getName(),
                        String.format(ann.msg(), ann.min(), ann.max()));
            }
        } else {
            Map map = (Map) model.getValue();
            if (map.size() > ann.max() || map.size() < ann.min()) {
                errors.put(model.getName(),
                        String.format(ann.msg(), ann.min(), ann.max()));
            }
        }
    }

    private void range(Model model, Range ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof Integer)) {
            errors.put(model.getName(), "该字段必须为整型");
            return;
        }
        Integer value = (Integer) model.getValue();
        if (value > ann.max() || value < ann.min()) {
            errors.put(model.getName(),
                    String.format(ann.msg(), ann.min(), ann.max()));
        }
    }

    private void pattern(Model model, Pattern ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof String)) {
            errors.put(model.getName(), "该字段必须为字符串类型");
            return;
        }
        String value = (String) model.getValue();
        if (!value.matches(ann.regex())) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void isNull(Model model, Null ann) {
        if (!model.isNull()) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void notNull(Model model, NotNull ann) {
        if (model.isNull()) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void notEmpty(Model model, NotEmpty ann) {

        if (!(model.getValue() instanceof String)) {
            errors.put(model.getName(), "该字段必须为字符串类型");
            return;
        }
        String value = (String) model.getValue();
        if (isEmpty(value)) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void notBlank(Model model, NotBlank ann) {

        if (!(model.getValue() instanceof String)) {
            errors.put(model.getName(), "该字段必须为字符串类型");
            return;
        }
        String value = (String) model.getValue();
        if (isBlank(value)) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void mobile(Model model, Mobile ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof String)) {
            errors.put(model.getName(), "该字段必须为字符串类型");
            return;
        }
        String value = (String) model.getValue();
        if (!value.matches(ann.regex())) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void min(Model model, Min ann) {

        if (model.isNull())
            return;
        if (!(model.getValue() instanceof Integer)) {
            errors.put(model.getName(), "该字段必须为整型");
            return;
        }
        Integer value = (Integer) model.getValue();
        if (value < ann.value()) {
            errors.put(model.getName(), String.format(ann.msg(), ann.value()));
        }
    }

    private void max(Model model, Max ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof Integer)) {
            errors.put(model.getName(), "该字段必须为整型");
            return;
        }
        Integer value = (Integer) model.getValue();
        if (value > ann.value()) {
            errors.put(model.getName(), String.format(ann.msg(), ann.value()));
        }
    }

    private void length(Model model, Length ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof String)) {
            errors.put(model.getName(), "该字段必须为字符串类型");
            return;
        }
        int len = ((String) model.getValue()).length();
        if (len > ann.max() || len < ann.min()) {
            errors.put(model.getName(),
                    String.format(ann.msg(), ann.min(), ann.max()));
        }
    }

    private void email(Model model, Email ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof String)) {
            errors.put(model.getName(), "该字段必须为字符串类型");
            return;
        }
        String value = (String) model.getValue();
        if (!value.matches(ann.value())) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void assertFalse(Model model, AssertFalse ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof Boolean)) {
            errors.put(model.getName(), "该字段必须为布尔类型");
            return;
        }
        if ((Boolean) model.getValue()) {
            errors.put(model.getName(), ann.msg());
        }
    }

    private void assertTrue(Model model, AssertTrue ann) {
        if (model.isNull())
            return;
        if (!(model.getValue() instanceof Boolean)) {
            errors.put(model.getName(), "该字段必须为布尔类型");
            return;
        }
        if (!(Boolean) model.getValue()) {
            errors.put(model.getName(), ann.msg());
        }
    }

    protected class Model {
        private Field field;
        private String name;
        private Object value;

        public Model(Field field, Object value) {
            this.field = field;
            this.name = field.getName();
            this.value = value;
        }

        public boolean isType(Class<?> clazz) {
            return field.getType().equals(clazz);
        }

        public boolean isNotType(Class<?> clazz) {
            return !isType(clazz);
        }

        public boolean isNull() {
            return value == null;
        }

        public Field getField() {
            return field;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }

    }


}
