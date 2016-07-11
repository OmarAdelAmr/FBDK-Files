<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE AdapterType SYSTEM "http://www.holobloc.com/xml/LibraryElement.dtd" >
<AdapterType Name="TEST_ADP" Comment="Adapter Interface for Testing" >
  <Identification Standard="61499-1" />
  <VersionInfo Organization="Holobloc Inc" Version="0.2" Author="JHC" Date="2005-02-18" Remarks="Moved from &#34;student&#34; to &#34;doc&#34;." />
  <VersionInfo Organization="Rockwell Automation" Version="0.1" Author="JHC" Date="2003-08-17" Remarks="Moved from &#34;book&#34; to &#34;student&#34;." />
  <VersionInfo Organization="Rockwell Automation" Version="0.0" Author="JHC" Date="2002-01-08" Remarks="Built from NEWADAPTER." />
  <CompilerInfo header="package fb.rt.doc;" >
    <Compiler Language="Java" Vendor="IBM" Product="Jikes" Version="1.06" />
  </CompilerInfo>
  <InterfaceList>
    <EventInputs>
      <Event Name="REQ" Comment="Request from Socket" >
        <With Var="REQD" />
      </Event>
    </EventInputs>
    <EventOutputs>
      <Event Name="CNF" Comment="Confirmation from Plug" >
        <With Var="CNFD" />
      </Event>
    </EventOutputs>
    <InputVars>
      <VarDeclaration Name="REQD" Type="WSTRING" Comment="Request Data from Socket" />
    </InputVars>
    <OutputVars>
      <VarDeclaration Name="CNFD" Type="WSTRING" Comment="Confirmation Data from Plug" />
    </OutputVars>
  </InterfaceList>
  <Service RightInterface="PLUG" LeftInterface="SOCKET" >
    <ServiceSequence Name="request_confirm" >
      <ServiceTransaction >
        <InputPrimitive Interface="SOCKET" Event="REQ" Parameters="REQD" />
        <OutputPrimitive Interface="PLUG" Event="REQ" Parameters="REQD" />
      </ServiceTransaction>
      <ServiceTransaction >
        <InputPrimitive Interface="PLUG" Event="CNF" Parameters="CNFD" />
        <OutputPrimitive Interface="SOCKET" Event="CNF" Parameters="CNFD" />
      </ServiceTransaction>
    </ServiceSequence>
  </Service>
</AdapterType>
