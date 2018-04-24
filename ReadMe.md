DBGUI
==
* A lib to build java database/gui small project easily.  
* Write java project just like write an android project.  
* A good simple for newer to lean the annotation and generic.  
* But the running speed is a little lower than raw code.  

说明
--
Intellij IDEA 2016.2.5 Project  
项目依赖[gson](https://github.com/google/gson)
/ [mysql-connector-java](https://www.mysql.com/products/connector/)。  
导入本库`dbgui.jar`(build_dbgui_jar目录下)，以及以上两个依赖库，即可使用。

使用-DB
--
1. 准备工作：请先在mysql中，创建数据库和数据表。
2. 数据库相关工具使用前需要进行一次初始化操作：
`Dbgui.initDatabase(dbName,userName,passWord)`。  
使用结束后，或者项目运行结束时，可以根据需要释放：
`Dbgui.releaseDatabase()`。  
3. 创建MODEL：为每一张表创建一个对应的Model类。  
类名的全小写要与数据表名一致（类名首字母可以大写，保证其全小写一致即可）。  
类中的每个字段（变量），需要与数据表对应的列完全一致，区分大小写。  
变量可见性随意。
Model中的自增主键，或者写入Model到数据库需要忽略的键，
请使用`@AutoIncrement`修饰。
4. 创建DAO：创建接口，对数据库进行操作，允许使用`@Query`注解和`@Modiy`注解。  
   * `@Query`注解：数据库查询注解，返回类型`List<Model>`。
   * `@Modiy`注解：数据库增删改注解，返回类型`boolean`。
   * 可以直接在注解的`value`项写sql语句，
     使用`?数字`代表要替换的参数，数字从1开始。
     ```
     @Query("slelct * from user where uid = ?1")
     List<User> getUserById(String uid);
     @Modify("delete from user where uid = ?1")
     boolean deleteUserById(String uid);
     ```
     查询全部和插入数据可以使用`table`属性。
     `@Query`可以使用`increaseSort`或`decreaseSort`进行排序，值为字段名。
     ```
     @Query(table = User.class, decreaseSort = 'uid')
     List<User> getAllUser();
     @Modify(table = User.class)
     boolean addUser(User user); //参数只能为单个Model对象
     ```
     高级用法：`@Query`可以使用`groupMerge`进行聚合，值为字段名。
     如果要以多列做参考，用井号（#）分隔。
     返回类型为`List<List<Model>>`。
     ```
     //返回值中，每组`List<User>`的年龄相同。
     @Query(table = User.class, groupMerge = 'age')
     List<List<User>> getAllUser();
     //返回值中，每组`List<User>`的性别和年龄年龄相同。
     //也就是说，该接口返回的分组数目，会比上个接口多。
     @Query(table = User.class, groupMerge = 'gender#age')
     List<List<User>> getAllUser();
     ```
     高级用法：`@Query`可以使用`onePage`指定分页。
     另注，分页属于后期处理分页，性能相对原生SQL分页可能慢一些。
     此时方法参数中需要添加一个参数`int pageNumber`作为最后一个参数。
     当调用时，传入`Query.ALL_PAGE`时返回所有结果，传入`1`为首页。
     可通过`GuiUtil.getPageCount(...)`获取全部结果的页数。
     ```
     @Query(table = User.class, onePage = 10)
     List<List<User>> getAllUser();
     ```
5. 创建`Service`类，使用`@AutoCreate`注解修饰需要自动生成的`DAO`接口对象。
`DAO`接口不必写其实现。
   ```
   public class UserService {
       @AutoCreate
       UserDao userDao;
   }
   ```
6. 创建`Service`类对象，调用`Dbgui.processAutoCreate(obj)`执行注解解析。
之后就可以使用`Service`类对象中的`DAO`了。
说明：如果`Service`类继承自本库的`gui`包下的界面元素，不必进行注解解析的调用。
   ```
   UserService userService = new UserService();
   Dbgui.processAutoCreate(userService);
   userService.getAllUsser();
   ```
7. 特殊的一些操作，可以直接使用`DatabaseUtil.getConnection()`
获取到数据库连接，然后自行进行数据库操作。

使用-GUI
--
在本库的`gui`包下，包含4个绝对布局组件，分别是
`AbsolutelyFrame`,`AbsolutelyPanel`,
`AbsolutelyModelDialog`,`AbsolutelyDialog`。
以上组件均使用绝对布局（即`setLayout(null)`），
通过与GUI相关注解配合使用，便于快速创建界面。  
1.  `@View`注解的`value`接收数组，内容定义如下。
可以修饰类中的变量，或者修饰`JFrame`，`JDialog`。
`scroll = true`可在外部自动添加`JScrollPane`。
    ```
    @View //仅初始化组件
    @View({"800,600"}) //表示大小
    @View({"0,0,800,600"}) //表示位置
    @View({"显示文本","位置或大小"})
    @View({"名称（setName）","显示文本","位置或大小"})
    ```
2. `@Click`添加监听，修饰方法。
方法参数允许为空，或者为`JButton`。
注解的`value`为按钮变量名，为空表示类中所有按钮。
   ```
   @Click //将类中所有按钮都绑定此方法
   void onAllButtonClick(JButton button){}
   @Click("btnAdd") //将变量`JButton btnAdd`绑定此方法。
   void onAddButtonClick(){}
   ```
3. `@CollectView`用于集合变量，
`type`指定需要集合的变量类型，
如果需要集合的类型继承自`JTextComponent`，
可以使用`editable = false`表示集合后禁用编辑。
修饰的变量类型为`List<需要集合的type>`，
或者使用view包下的`EditableGroup`,`ClickableGroup`。
   ```
   @CollectView(type = JTextField.class)
   EditableGroup editableGroup;
   @CollectView(type = JTextField.class)
   List<JTextField> textFieldGroup;
   ```
4. `@ViewBinder(index)`与`@View(index)`对应，以及`@DialogSuccess`，
可以用于`AbsolutelyModelDialog`中。  
类继承自`AbsolutelyModelDialog<Model>`后，
确认对话框返回成功的按钮用`@DialogSuccess`修饰，
类中组件用`@View`修饰时，可以添加`index`属性，
在`Model`类中的变量使用`@ViewBinder(index)`与对应组件
设置相同的`index`。
这样，当成功按钮点击后，将自动将界面内`index`对应的编辑组件
的值，写入到`Model`对象对应`index`的变量，
并在`dialogModelInfo`中将该对象返回。  
对话框使用显示：
```
new MyDialog().setSuccessListener(...).showSelf();
```
5. 直接继承自`swing`组件的类，
需要在构造器使用`Dbgui.processView(this);`
进行注解解析。

使用-VIEW
--
自定义的VIEW主要是列表，其中适配器列表类`ModelTableList`，
类似于安卓的自定义列表。（Nx1）
另外，请优先考虑JList和JTable，最后考虑本组件。  
对于自定义的列表对象，若继承自`ModelTableList`，
可通过`updateItems(List)`进行数据显示。
并通过注解`@ViewBinder(index)`与`@View(index)`进行数据绑定，
直接将`Model`的数据显示到对应的`View`，
而不必在`bindModelHolder(...)`方法中手动设置数据。  
继承自`AdapterTableList`或`AdapterVerticalList`的类，
通过`notifyChanged()`进行数据显示。
另外，由于`Java`有时候存在UI不刷新问题，
可以调用`scrollToTop()`确保刷新。
注解可以对列表类进行修饰：
1. `@ListTitle`指定列表标题栏。缺省表示无标题栏。
2. `@ListItemWidth`指定列表每列宽度。缺省表示列均分宽度。
3. `@ListItemHeight`这顶列表最小行高。缺省表示默认最小行高。
若`ViewHolder`重写方法`getAbsolutelyHeight()`，
返回值大于最小行高时，将使用返回值 最为行高。
4. `@ListColumnCount`指定列数。缺省表示单列。
若指定了`@ListTitle`则本参数被忽略。
若指定了`@ListItemWidth`，但数目小于该列数，多余的列将均分剩余宽度。
5. `@ListBorder`指定列表边框粗细和颜色。缺省使用默认边框。
6. 组件`PagePanel`为简易翻页栏，可直接添加到界面，
通过`setPageListener`设定页面跳转监听，通过`setPage`设置初始页面或跳转页面即可。

使用-UTIL
--
1. `DbGui`数据库配置、注解解析相关的工具。
2. `GuiUtil`界面相关的常用工具。
推荐一个`GuiUtil.showToast`，类似`android.widget.Toast`，
非常有趣。
