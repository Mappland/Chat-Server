package top.mappland.chat.config;

public class DataSourceContextHolder {
    private static final ThreadLocal<DataSourceKey> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDataSourceKey(DataSourceKey key) {
        CONTEXT_HOLDER.set(key);
    }

    public static DataSourceKey getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }
}
