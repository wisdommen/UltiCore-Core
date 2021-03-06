package com.ultikits.utils;

import com.ultikits.main.UltiCoreAPI;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseUtils {
    private final UltiCoreAPI ultiCoreAPI;
    private final String ip;
    private final String port;
    private final String username;
    private final String password;
    private final String database;

    private final DataSource dataSource;

    public DatabaseUtils(UltiCoreAPI ultiCoreAPI) {
        this.ultiCoreAPI = ultiCoreAPI;
        ip = ultiCoreAPI.getDatabaseIP();
        port = ultiCoreAPI.getDatabasePort();
        username = ultiCoreAPI.getDatabaseUsername();
        password = ultiCoreAPI.getDatabasePassword();
        database = ultiCoreAPI.getDatabaseName();
        if (ultiCoreAPI.isDatabaseEnabled()) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8&useSSL=false");
            config.setUsername(username);
            config.setPassword(password);
            config.addDataSourceProperty("connectionTimeout", "2000"); // 连接超时：1秒
            config.addDataSourceProperty("idleTimeout", "60000"); // 空闲超时：60秒
            config.addDataSourceProperty("maximumPoolSize", "10"); // 最大连接数：10
            dataSource = new HikariDataSource(config);
        } else {
            dataSource = null;
        }
    }

    /**
     * 获取JDBC连接池
     *
     * @return JDBC连接池
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 创建一个数据表
     *
     * @param tableName 数据表名称
     * @param fields    数据表表头
     */
    public boolean createTable(String tableName, String[] fields) {
        return createTable(tableName, fields, true);
    }

    /**
     * 创建一个数据表
     *
     * @param tableName  数据表名称
     * @param fields     数据表表头
     * @param autoCommit 开关自动提交机制
     */
    public boolean createTable(String tableName, String[] fields, boolean autoCommit) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(autoCommit);
            try (PreparedStatement ps = connection.prepareStatement("create table if not exists " + tableName + "(" + getFields(fields) + ")")) {
                ps.executeUpdate();
                if (!autoCommit) {
                    connection.commit();
                }
                return true;
            } catch (Exception e) {
                if (!autoCommit) {
                    connection.rollback();
                }
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param tableName  数据表名称
     * @param columnName 需要查看的列
     * @return 是否存在这个列
     */
    public boolean isColumnExists(String tableName, String columnName) {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet re = statement.executeQuery("desc " + tableName + " " + columnName)) {
                    return re.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param tableName  数据表名称
     * @param columnName 需要添加的列
     * @return 是否添加成功
     */
    public boolean addColumn(String tableName, String columnName) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement("ALTER TABLE " + tableName); PreparedStatement ps2 = connection.prepareStatement("add column " + columnName + " TEXT")) {
                ps.executeUpdate();
                int b = ps2.executeUpdate();
                return (b == 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取数据记录
     *
     * @param id        操作对象id
     * @param tableName 表名称
     * @param fieldName 列名称
     * @return 获取的数据，若未找到数据则返回null
     */
    public @Nullable String getData(String primaryIDField, String id, String tableName, String fieldName) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("select " + fieldName + " from " + tableName + " where " + primaryIDField + "=?")) {
                ps.setString(1, id);
                try (ResultSet re = ps.executeQuery()) {
                    if (re.next()) {
                        return re.getString(fieldName);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取某列所有数据
     *
     * @param tableName 表名
     * @param fieldName 需要获取的列
     * @return 含有数据的列表
     */
    public @NotNull List<String> getKeys(String tableName, String fieldName) {
        List<String> keys = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("select " + fieldName + " from " + tableName)) {
                try (ResultSet re = ps.executeQuery()) {
                    while (re.next()) {
                        keys.add(re.getString(fieldName));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keys;
    }

    /**
     * 插入记录
     *
     * @param tableName 数据表名称
     * @param dataMap   待插入的数据集
     * @return 是否成功
     */
    public boolean insertData(String tableName, Map<String, String> dataMap) {
        return insertData(tableName, dataMap, true);
    }

    /**
     * 插入记录
     *
     * @param tableName  数据表名称
     * @param dataMap    待插入的数据集
     * @param autoCommit 开关自动提交机制
     * @return 是否成功
     */
    public boolean insertData(String tableName, @NotNull Map<String, String> dataMap, boolean autoCommit) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(autoCommit);
            String[] keys = dataMap.keySet().toArray(new String[0]);
            String[] values = dataMap.values().toArray(new String[0]);
            try (PreparedStatement ps = connection.prepareStatement("insert into " + tableName + " (" + insertFields(keys) + ") values (" + insertValues(values) + ")")) {
                int a = ps.executeUpdate();
                if (!autoCommit) {
                    connection.commit();
                }
                return (a == 1);
            } catch (Exception e) {
                if (!autoCommit) {
                    connection.rollback();
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 使用默认值更新数据
     *
     * @param tableName      数据表名称
     * @param fieldName      列名称
     * @param id             记录id
     * @param primaryIDField 表唯一ID
     * @param value          更新值
     * @return 是否成功
     */
    public boolean updateData(String tableName, String fieldName, String primaryIDField, String id, String value) {
        return updateData(tableName, fieldName, primaryIDField, id, value, true, null);
    }

    /**
     * 更新数据
     *
     * @param tableName       数据表名称
     * @param fieldName       列名称
     * @param primaryIDField  表唯一ID
     * @param id              记录id
     * @param value           更新值
     * @param autoCommit      自动提交
     * @param otherStatements 其他需要执行的SQL语句
     * @return 是否成功
     */
    public boolean updateData(String tableName, String fieldName, String primaryIDField, String id, String value, boolean autoCommit, List<PreparedStatement> otherStatements) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(autoCommit);
            try (PreparedStatement ps = connection.prepareStatement("update " + tableName + " set " + fieldName + "=? " + " where " + primaryIDField + "=?")) {
                ps.setString(1, value);
                ps.setString(2, id);
                int a = ps.executeUpdate();
                if (otherStatements != null) {
                    for (PreparedStatement each : otherStatements) {
                        if (each != null) {
                            each.executeUpdate();
                        }
                    }
                }
                if (!autoCommit) {
                    connection.commit();
                }
                return (a == 1);
            } catch (Exception e) {
                if (!autoCommit) {
                    connection.rollback();
                }
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成待处理的预处理statement
     *
     * @param tableName      数据表名称
     * @param fieldName      列名称
     * @param primaryIDField 表唯一ID
     * @param id             记录id
     * @param value          更新值
     * @return 是否成功
     */
    public @Nullable PreparedStatement updateDataWait(String tableName, String fieldName, String primaryIDField, String id, String value) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement("update " + tableName + " set " + fieldName + "=? " + " where " + primaryIDField + "=?")) {
                ps.setString(1, value);
                ps.setString(2, id);
                return ps;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询记录是否存在
     *
     * @param tableName      表名称
     * @param primaryIDField 唯一ID表头名称
     * @param id             需要查询的记录的id
     * @return 是否存在这条记录
     */
    public boolean isRecordExists(String tableName, String primaryIDField, String id) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("select 1 from " + tableName + " where " + primaryIDField + "=? limit 1")) {
                ps.setString(1, id);
                try (ResultSet re = ps.executeQuery()) {
                    return re.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将一个数据增加一定的量
     *
     * @param tableName      表名称
     * @param fieldName      表头名称
     * @param primaryIDField 唯一ID表头名称
     * @param id             ID
     * @param value          增加的量
     * @return 成功/失败
     */
    public boolean increaseData(String tableName, String fieldName, String primaryIDField, String id, String value) {
        return increaseData(tableName, fieldName, primaryIDField, id, value, true, null);
    }

    /**
     * 将一个数据增加一定的量
     *
     * @param tableName       表名称
     * @param fieldName       表头名称
     * @param primaryIDField  唯一ID表头名称
     * @param id              ID
     * @param value           增加的量
     * @param autoCommit      开关自动提交
     * @param otherStatements 其他需要处理的PreparedStatement
     * @return 成功/失败
     */
    public boolean increaseData(String tableName, String fieldName, String primaryIDField, String id, String value, boolean autoCommit, List<PreparedStatement> otherStatements) {
        String dataStringBefore = getData(primaryIDField, id, tableName, fieldName);
        try {
            assert dataStringBefore != null;
            double dataBefore = Double.parseDouble(dataStringBefore);
            double dataAdded = Double.parseDouble(value);
            double dataAfter = dataBefore + dataAdded;
            String dataAfterString = String.valueOf(dataAfter);
            updateData(tableName, fieldName, primaryIDField, id, dataAfterString, autoCommit, otherStatements);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将一个数据增加一定的量，但并不执行
     *
     * @param tableName      表名称
     * @param fieldName      表头名称
     * @param primaryIDField 唯一ID表头名称
     * @param id             ID
     * @param value          增加的量
     * @return 一条未执行的预处理statement
     */
    public @Nullable PreparedStatement increaseDataStandby(String tableName, String fieldName, String primaryIDField, String id, String value) {
        String dataStringBefore = getData(primaryIDField, id, tableName, fieldName);
        try {
            assert dataStringBefore != null;
            double dataBefore = Double.parseDouble(dataStringBefore);
            double dataAdded = Double.parseDouble(value);
            double dataAfter = dataBefore + dataAdded;
            String dataAfterString = String.valueOf(dataAfter);
            return updateDataWait(tableName, fieldName, primaryIDField, id, dataAfterString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将一个数据减少一定的量
     *
     * @param tableName      表名称
     * @param fieldName      表头名称
     * @param primaryIDField 唯一ID表头名称
     * @param id             ID
     * @param value          减少的量
     * @return 成功/失败
     */
    public boolean decreaseData(String tableName, String fieldName, String primaryIDField, String id, String value) {
        return decreaseData(tableName, fieldName, primaryIDField, id, value, true, null);
    }

    /**
     * 将一个数据减少一定的量
     *
     * @param tableName       表名称
     * @param fieldName       表头名称
     * @param primaryIDField  唯一ID表头名称
     * @param id              ID
     * @param value           减少的量
     * @param autoCommit      开关自动提交
     * @param otherStatements 其他需要处理的PreparedStatement
     * @return 成功/失败
     */
    public boolean decreaseData(String tableName, String fieldName, String primaryIDField, String id, String value, boolean autoCommit, List<PreparedStatement> otherStatements) {
        String dataStringBefore = getData(primaryIDField, id, tableName, fieldName);
        try {
            assert dataStringBefore != null;
            double dataBefore = Double.parseDouble(dataStringBefore);
            double dataDec = Double.parseDouble(value);
            double dataAfter = dataBefore - dataDec;
            String dataAfterString = String.valueOf(dataAfter);
            return updateData(tableName, fieldName, primaryIDField, id, dataAfterString, autoCommit, otherStatements);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将一个数据减少一定的量，但并不执行
     *
     * @param tableName      表名称
     * @param fieldName      表头名称
     * @param primaryIDField 唯一ID表头名称
     * @param id             ID
     * @param value          减少的量
     * @return 一条未执行的预处理statement
     */
    public @Nullable PreparedStatement decreaseDataStandby(String tableName, String fieldName, String primaryIDField, String id, String value) {
        String dataStringBefore = getData(primaryIDField, id, tableName, fieldName);
        try {
            assert dataStringBefore != null;
            double dataBefore = Double.parseDouble(dataStringBefore);
            double dataDec = Double.parseDouble(value);
            double dataAfter = dataBefore - dataDec;
            String dataAfterString = String.valueOf(dataAfter);
            return updateDataWait(tableName, fieldName, primaryIDField, id, dataAfterString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将一个String[]转换为mysql建表语句
     *
     * @param fields 需要转换的fields
     * @return 一个MySQL语句
     */
    private static @NotNull String getFields(String[] fields) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String arg : fields) {
            ++i;
            builder.append(arg + " TEXT" + (i == fields.length ? "" : ","));
        }
        return builder.toString();
    }

    /**
     * 将一个String[]转换为mysql插入语句的fields
     *
     * @param fields 需要转换的fields
     * @return 一个MySQL语句
     */
    private static @NotNull String insertFields(String[] fields) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String arg : fields) {
            ++i;
            builder.append(arg + (i == fields.length ? "" : ","));
        }
        return builder.toString();
    }

    /**
     * 将一个String[]转换为mysql插入语句的values
     *
     * @param values 需要转换的fields
     * @return 一个MySQL语句
     */
    private static @NotNull String insertValues(String[] values) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (String arg : values) {
            ++i;
            builder.append("'" + arg + "'" + (i == values.length ? "" : ","));
        }
        return builder.toString();
    }
}
