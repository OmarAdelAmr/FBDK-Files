<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ResourceType SYSTEM "http://www.holobloc.com/xml/LibraryElement.dtd" >
<ResourceType Name="EMB_RES2" Comment="An Embedded Resource Type with a FBD installed" >
  <Identification Standard="61499-1" Function="Device Management" Description="A simple embedded resource" />
  <VersionInfo Organization="Rockwell Automation" Version="0.0" Author="JHC" Date="2001-01-04" />
  <CompilerInfo header="import fb.rt.math.*;" >
    <Compiler Language="Java" Vendor="IBM" Product="VisualAge" Version="3.0" />
  </CompilerInfo>
  <FBNetwork >
    <FB Name="START" Type="E_RESTART" x="11.111111" y="11.111111" />
    <FB Name="FB1" Type="FB_ADD_REAL" x="616.6666" y="11.111111" >
      <Parameter Name="IN1" Value="1.0" />
      <Parameter Name="IN2" Value="2.0" />
    </FB>
    <FB Name="FB2" Type="FB_SUB_REAL" x="1283.3333" y="11.111111" >
      <Parameter Name="IN2" Value="4.0" />
    </FB>
    <EventConnections>
      <Connection Source="START.COLD" Destination="FB1.REQ" dx1="38.888885" />
      <Connection Source="FB1.CNF" Destination="FB2.REQ" dx1="38.888885" />
    </EventConnections>
    <DataConnections>
      <Connection Source="FB1.OUT" Destination="FB2.IN1" dx1="150.0" />
    </DataConnections>
  </FBNetwork>
</ResourceType>
