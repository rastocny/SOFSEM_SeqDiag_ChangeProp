# Automated Change Propagation from Source Code to Sequence Diagrams

Repository contains souce code, built jars and replication package of the tool presented in the paper "Automated Change Propagation from Source Code to Sequence Diagrams", in proceedings SOFSEM 2017.

## Prototype installation

1. Install [Eclipse Luna](http://www.eclipse.org/luna/)
2. Install [Papyrus modeling environment](http://www.eclipse.org/papyrus/download.html) to the Eclipse
3. Install [MoDisco extension](http://wiki.eclipse.org/MoDisco/Installation)
4. Copy .jar files from the directory `Build` to the directory `%ECLIPSE_DIR%\plugins`
5. (Re-)Start Eclipse Luna

## Configuration

You can make your own configuration of the synchronization plugins fy defining configuration file `%ECLIPSE_DIR%\plugins\configuration\com.mlyncar.dp.synch\synchronization.properties`. You can specify:

* Log file with details of a synchronization: `synch.changelog=D:/workspace/synch.log`
* Maximal number of allowed lifelines in a sequence diagram: `lifeline.max=5`

Example of [configuration file](https://github.com/rastocny/SOFSEM_SeqDiag_ChangeProp/blob/master/com.mlyncar.dp.synch/resources/synchronization.properties):

```
synch.changelog=D:/workspace/test.log
lifeline.max=100
```

## Sequence diagram synchronization

Synchronization is avaiable for `Java Standard Edition` projects, that have UML models (in files with suffixes "_notation" and "_uml"). You can execute synchronization by selecting project and pressing button `Synchronization Menu->Synchronize Sequence Diagrams`. If there is not any synchronization error, you will see the message informing about successfully finished synchronization. Otherwise you you will see a message with the synchronization error. Details of the error are logged to the file `workspace\.metadata\.log`

## Evaluation replication

Detailed description of test cases used in the evaluation is in the file [TestCasesDescription.pdf](https://github.com/rastocny/SOFSEM_SeqDiag_ChangeProp/ReplicationPackage/TestCasesDescription.pdf). Each test case is described by:

* original sequence diagram
* original source code
* description of source code modification
* modified source code
* description of synchronization results
* synchronization log
* resulted synchronized sequence diagram

To replicate the evaluation, testing projects are stored in the directory [TestProjects](https://github.com/rastocny/SOFSEM_SeqDiag_ChangeProp/ReplicationPackage/TestProjects). You can open the projects in Eclipse Luna as a worskpace and execute synchronization as it is described [above](#sequence-diagram-synchronization). Then you can manually validate results of synchronization by observing changes in sequence diagrams.
