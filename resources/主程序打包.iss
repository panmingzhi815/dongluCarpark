; �ű��� Inno Setup �ű��� ���ɣ�
; �йش��� Inno Setup �ű��ļ�����ϸ��������İ����ĵ���

#define MyAppName "ͣ��������ʶ��"
#define MyAppVersion "1.0.0.33"
#define MyAppPublisher "��½����ʵҵ���޹�˾"
#define MyAppURL "http://www.dongluhitec.com/"
#define MyDateTimeString GetDateTimeString('_yyyymmddhhnnss', '-', ':')

[Setup]
; ע: AppId��ֵΪ������ʶ��Ӧ�ó���
; ��ҪΪ������װ����ʹ����ͬ��AppIdֵ��
; (�����µ�GUID����� ����|��IDE������GUID��)
AppId={{5F6B2625-1F5E-407B-870C-4D7DF4B17CFF}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName=D:\Program Files\{#MyAppName}
DefaultGroupName={#MyAppName}
DisableProgramGroupPage=yes
OutputDir=.
OutputBaseFilename=ͣ��������ʶ��{#MyAppVersion}{#MyDateTimeString}
Compression=lzma
SolidCompression=yes


[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "������.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "������.ini"; DestDir: "{app}"; Flags: ignoreversion
Source: "���·�ʽ.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "���¸���.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "�ͻ���.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "�ͻ���.ini"; DestDir: "{app}"; Flags: ignoreversion
Source: "����˵��.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "bin\*"; DestDir: "{app}\bin"; Flags: ignoreversion recursesubdirs createallsubdirs
; ע��: ��Ҫ���κι���ϵͳ�ļ���ʹ�á�Flags: ignoreversion��

;ɾ���ɰ汾�ļ�
[installDelete]
Type: filesandordirs; Name:"{app}\bin\jar"
Type: filesandordirs; Name:"{app}\bin\native"
;��ʼ�˵���ݷ�ʽ�� 
[Icons]
Name: "{group}\ͣ����������"; Filename: "{app}\������.exe";WorkingDir: "{app}"
Name: "{group}\ͣ�����ͻ���"; Filename: "{app}\�ͻ���.exe";WorkingDir: "{app}" 
;�����ݷ�ʽ�� 
Name: "{userdesktop}\ͣ����������"; Filename: "{app}\������.exe"; WorkingDir: "{app}"
Name: "{userdesktop}\ͣ�����ͻ���"; Filename: "{app}\�ͻ���.exe"; WorkingDir: "{app}"  
;��ʼ�˵�ж�ؿ�ݷ�ʽ�� 
Name: "{group}\{cm:UninstallProgram,ͣ��������ʶ��}"; Filename: "{uninstallexe}"

[registry]
Root: HKCU; Subkey: "SOFTWARE\Microsoft\Windows NT\CurrentVersion\AppCompatFlags\Layers"; ValueType: string; ValueName: "{app}\�ͻ���.exe"; ValueData: "RUNASADMIN"
Root: HKCU; Subkey: "SOFTWARE\Microsoft\Windows NT\CurrentVersion\AppCompatFlags\Layers"; ValueType: string; ValueName: "{app}\������.exe"; ValueData: "RUNASADMIN"

[Run]Filename: "{app}\������.exe"; Description: "{cm:LaunchProgram,ͣ����������}"; Flags: nowait postinstall skipifsilent
Filename: "{app}\�ͻ���.exe"; Description: "{cm:LaunchProgram,ͣ�����ͻ���}"; Flags: nowait postinstall skipifsilent

[code]
function NextButtonClick(CurPageID: Integer): Boolean;
var ResultCode: Integer;
var IsSetup : Boolean;
var AppPath:String;
var Search:String;
begin
IsSetup := true ;
  if CurPageID>1  then
  begin
    AppPath:=ExpandConstant('{app}');
    if Pos('C:',AppPath)>0  then
    begin
      MsgBox('��װ������װ��C��:'+AppPath,mbInformation,MB_OK);
      IsSetup := false ;
    end
    if Pos('c:',AppPath)>0   then
    begin
      MsgBox('��װ������װ��C��:'+AppPath,mbInformation,MB_OK);
      IsSetup := false ;
    end
  end

Result := IsSetup;
end;   