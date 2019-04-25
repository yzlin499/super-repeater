package top.yzlin.superrepeater.pythonparse;

import com.alibaba.fastjson.JSONObject;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import top.yzlin.superrepeater.BaseMethodEvent;
import top.yzlin.superrepeater.SimpleHttpAPI;
import top.yzlin.superrepeater.database.DBManager;
import top.yzlin.superrepeater.log.LogOperate;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.python.core.Py.None;

public class PythonMethodEvent extends BaseMethodEvent {
    private String groupID;
    private LogOperate logOperate;
    private SimpleHttpAPI simpleHttpAPI;
    private PythonInterpreter interpreter;
    private DBManager dbManager;

    public void setInterpreter(PythonInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void setDbManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setLogOperate(LogOperate logOperate) {
        this.logOperate = logOperate;
    }

    public void setSimpleHttpAPI(SimpleHttpAPI simpleHttpAPI) {
        this.simpleHttpAPI = simpleHttpAPI;
    }

    public void init() {
        PyFunction check = interpreter.get("check", PyFunction.class);
        setCheckFunction(makeCheck(check));
        PyFunction operate = interpreter.get("operate", PyFunction.class);
        setOperateFunction(makeOperate(operate));


    }


    private Predicate<JSONObject> makeCheck(PyFunction check) {
        PyTuple args = (PyTuple) check.__code__.__getattr__("co_varnames");
        String[] argNames = new String[args.size()];
        for (int i = 0; i < argNames.length; i++) {
            argNames[i] = args.get(i).toString();
        }
        return j -> {
            PyObject pyObject = makeParam(argNames, check, j);
            if (pyObject instanceof PyBoolean) {
                return ((PyBoolean) pyObject).getBooleanValue();
            } else if (pyObject instanceof PyInteger) {
                return ((PyInteger) pyObject).getValue() == 1;
            } else if (pyObject instanceof PyString) {
                return Objects.equals(pyObject.toString(), j.getString("message"));
            } else {
                return false;
            }
        };
    }

    private Consumer<JSONObject> makeOperate(PyFunction operate) {
        PyTuple args = (PyTuple) operate.__code__.__getattr__("co_varnames");
        String[] argNames = new String[args.size()];
        for (int i = 0; i < argNames.length; i++) {
            argNames[i] = args.get(i).toString();
        }
        return j -> {
            PyObject pyObject = makeParam(argNames, operate, j);
            if (!(pyObject instanceof PyNone)) {
                simpleHttpAPI.sendGroupMsg(groupID, pyObject.toString());
            }
        };
    }

    private PyObject makeParam(String[] argNames, PyFunction check, JSONObject j) {
        PyObject[] argObj = new PyObject[argNames.length];
        for (int i = 0; i < argObj.length; i++) {
            String a = j.getString(argNames[i]);
            argObj[i] = a != null ? new PyString(a) : None;
        }
        return check.__call__(argObj, argNames);
    }

}
