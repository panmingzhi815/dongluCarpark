; 脚本由 Inno Setup 脚本向导 生成！
; 有关创建 Inno Setup 脚本文件的详细资料请查阅帮助文档！

#define MyAppName "java虚拟机1.0"
#define MyAppVersion "1.0"
#define MyAppPublisher "东陆高新实业有限公司"
#define MyAppURL "http://www.dongluhitec.com/"

[Setup]
; 注: AppId的值为单独标识该应用程序。
; 不要为其他安装程序使用相同的AppId值。
; (生成新的GUID，点击 工具|在IDE中生成GUID。)
AppId={{5784066D-2788-4F3E-8693-7C8CCA242DF3}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\java
DefaultGroupName=java虚拟机
DisableProgramGroupPage=yes
OutputDir=.
OutputBaseFilename=java虚拟机1.0
Compression=lzma
SolidCompression=yes
AlwaysRestart=yes

[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "jre\*"; DestDir: "{app}\jre"; Flags: ignoreversion recursesubdirs createallsubdirs
; 注意: 不要在任何共享系统文件上使用“Flags: ignoreversion”

[installDelete]
Type: filesandordirs; Name:"{app}\jre"
[Icons]
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"

[registry]
;本段处理程序在注册表中的键值
Root:HKLM;Subkey:SYSTEM\CurrentControlSet\Control\Session Manager\Environment;ValueType: string; ValueName:DONGLU_JRE;ValueData:{app}\jre;Flags: uninsdeletevalue


