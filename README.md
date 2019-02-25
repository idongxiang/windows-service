### Windows Service 安装

https://github.com/kohsuke/winsw

#### Step 1

下载WinSW.NET4.exe

https://github.com/kohsuke/winsw/releases

#### Step 2

mvn package

创建windows-service.xml

```
<service>
    <id>windows-service</id>
    <name>windows-service</name>
    <description>This service runs Windows CommandLine.</description>
    <env name="JAVA_HOME" value="%JAVA_HOME%"/>
    <env name="WINDOWS_SERVICE_HOME" value="%BASE%"/>
    <executable>java</executable>
    <arguments>-Xrs -Xmx256m -jar "%BASE%\windows-service.jar"</arguments>
    <startmode>Automatic</startmode>
    <logmode>rotate</logmode>
</service>
```

#### Step 3

将WinSW.NET4.exe,indows-service-1.0-SNAPSHOT.jar,windows-service.xml放到同一文件夹

重命名 windows-service-1.0-SNAPSHOT.jar 为 windows-service.jar
重命名 inSW.NET4.exe 为 windows-service.exe


#### Step 4

注册服务,CMD切换到文件目录执行命令

windows-service.exe install <OPTIONS>

```
F:\Deploy>windows-service.exe install
2019-02-22 00:04:32,390 INFO  - Installing the service with id 'windows-service'
2019-02-22 00:04:32,410 FATAL - WMI Operation failure: AccessDenied
WMI.WmiException: AccessDenied
   在 WMI.WmiRoot.BaseHandler.CheckError(ManagementBaseObject result)
...
```
如果报如上错误,请以管理员身份打开CMD

安装成功后,win+R打开运行,输入services.msc,查看windows-service服务,运行状态是否为正在运行。

Usage
WinSW is being managed by configuration files: Main XML Configuration file and EXE Config file.

Your renamed winsw.exe binary also accepts the following commands:

**install** to install the service to Windows Service Controller. This command requires some preliminary steps described in the Installation Guide.

**uninstall** to uninstall the service. The opposite operation of above.

**star**t to start the service. The service must have already been installed.

**stop** to stop the service.

**restart** to restart the service. If the service is not currently running, this command acts like start.

**status** to check the current status of the service.

This command prints one line to the console.
- NonExistent indicates the service is not currently installed
- Started to indicate the service is currently running
- Stopped to indicate that the service is installed but not currently running.