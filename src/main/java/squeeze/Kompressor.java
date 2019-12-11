package squeeze;

import picocli.CommandLine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CommandLine.Command(
        name = "archiver",
        description = "Archive a file or directory."
)

public class Kompressor implements Callable<Integer> {

    @CommandLine.Option(names = {"-o", "--output"}, required = true, description = "Name of archive.")
    public String output;

    @CommandLine.Option(names = {"-i", "--input"}, required = true, description = "File or directory to archive.")
    public File input;

    public static void main(String[] args) {
        int exitCode;
        exitCode = new CommandLine(new Kompressor()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        System.out.println(System.getProperty("user.dir"));
        FileOutputStream fos = new FileOutputStream(output);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zipFile(input, input.getName(), zos);
        //fos.close();
        zos.close();
        return 0;
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if(fileToZip.isDirectory()) {
            if(fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();
            File [] children = fileToZip.listFiles();
            for(File c : Objects.requireNonNull(children)) {
                zipFile(c, fileName + "/" + c.getName(), zipOut);
            }
            return;
        }
        //FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry e = new ZipEntry(fileName);
        zipOut.putNextEntry(e);
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        zipOut.write(bytes, 0, bytes.length);
    }
}