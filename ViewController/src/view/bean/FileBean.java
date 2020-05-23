package view.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCIteratorBinding;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.binding.BindingContainer;

import oracle.binding.OperationBinding;

import oracle.jbo.domain.BlobDomain;

import org.apache.myfaces.trinidad.model.UploadedFile;



public class FileBean {

    private static final String PATH_DOWNLOAD = "~//Download//";
    private static final String NAME_VIEW_ITERATOR = "FilesView1Iterator";
    
    private RichInputFile inputFileComponent;


    public FileBean() {
    }

    private void writeInputStreamToOutputStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    private BlobDomain newBlobDomainForInputStream(InputStream in) throws SQLException, IOException {
        BlobDomain     b = new BlobDomain();
        OutputStream out = b.getBinaryOutputStream();
              writeInputStreamToOutputStream(in, out);
                      in.close();
                     out.close();
        return b;
    }

    public void upload(ValueChangeEvent valueChangeEvent) {
//        System.out.println("public void upload(ValueChangeEvent valueChangeEvent)");    
        try {
            UploadedFile      file     = (UploadedFile) valueChangeEvent.getNewValue();
            String            fileName = file.getFilename();
            BindingContainer  bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
            DCIteratorBinding iter     = (DCIteratorBinding) bindings.get("NAME_VIEW_ITERATOR");
                              iter.getCurrentRow().setAttribute("Nam", fileName);
                              iter.getCurrentRow().setAttribute("Data", newBlobDomainForInputStream(file.getInputStream()));
            OperationBinding  op       = bindings.getOperationBinding("Commit");
                              op.execute();

            FacesContext      context  = FacesContext.getCurrentInstance();
                              context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Файл завантажено успішно..", null));
            getInputFileComponent().resetValue();

          }
          catch (Exception e) {
            // TODO: Add catch code
            e.printStackTrace();
          }
    }

    private String uploadFileToLocation(UploadedFile file)    {
      String               fileName = System.currentTimeMillis() + "_" + file.getFilename();
      String                   path = PATH_DOWNLOAD + fileName;
      InputStream       inputStream = null;
      
      try      {
        FileOutputStream         out = new FileOutputStream(path);
                         inputStream = file.getInputStream();
        byte[]                buffer = new byte[8192];
        int                bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1)      {
                 out.write(buffer, 0, bytesRead);
        }
                 out.flush();
                 out.close();
      }
      catch (Exception ex)   {
        // handle exception
        ex.printStackTrace();
      }
      finally      {
        try        {
          inputStream.close();
        }
        catch (IOException e) {
        }
      }
      return fileName;
    }

    public void uploadFileDisk(ValueChangeEvent valueChangeEvent)   {
      String           uploadedFileName = uploadFileToLocation ((UploadedFile)valueChangeEvent.getNewValue());
      BindingContainer         bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
      DCIteratorBinding            iter = (DCIteratorBinding) bindings.get(NAME_VIEW_ITERATOR);
                                   iter.getCurrentRow().setAttribute("Nam", uploadedFileName);
      OperationBinding               op = bindings.getOperationBinding("Commit");
                                     op.execute();
                getInputFileComponent().resetValue();
    }

    public void uploadFile(ValueChangeEvent valueChangeEvent)      {
        String           uploadedFileName = uploadFileToLocation ((UploadedFile)valueChangeEvent.getNewValue());
        BindingContainer         bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding            iter = (DCIteratorBinding) bindings.get(NAME_VIEW_ITERATOR);
                                     iter.getCurrentRow().setAttribute("Nam", uploadedFileName);
        OperationBinding               op = bindings.getOperationBinding("Commit");
                                       op.execute();
                  getInputFileComponent().resetValue();
      }

      
      public void downloadDocDisk(FacesContext facesContext, java.io.OutputStream outputStream) throws IOException   {
        BindingContainer bindings = BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding          iter = (DCIteratorBinding) bindings.get(NAME_VIEW_ITERATOR);
        String                     path = PATH_DOWNLOAD + iter.getCurrentRow().getAttribute("Nam");
        File                       file = new File(path);
        byte[]                        b = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
                        fileInputStream.read(b);

                           outputStream.write(b);
                           outputStream.flush();
      }

    public void setInputFileComponent(RichInputFile inputFileComponent)    {
      this.inputFileComponent = inputFileComponent;
    }

    public RichInputFile getInputFileComponent()    {
      return inputFileComponent;
    }

}
