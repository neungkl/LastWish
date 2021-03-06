package resource;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import render.image.ImageData;
import exception.ResourceException;

public class ImageReader {
	
	private static ClassLoader loader = ImageReader.class.getClassLoader();
	private ImageReader() {}
	
	public static ImageData[] get(String url) throws ResourceException {
		
		ImageData[] frame = null;
		
		String extension = url.substring(url.indexOf(".", 0) + 1,url.length());
		
		if(extension.equals("gif")) {
		
			try {
			    javax.imageio.ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
			    ImageInputStream stream = ImageIO.createImageInputStream(loader.getResourceAsStream(url));;
			    reader.setInput(stream);
			    
			    int count = reader.getNumImages(true);
			    frame = new ImageData[count];
			    
			    for (int i = 0; i < count; i++) {
			    	
			    	BufferedImage image = reader.read(i);
			    	frame[i] = new ImageData(image);
			    	
			        NodeList child = reader.getImageMetadata(i).getAsTree("javax_imageio_gif_image_1.0").getChildNodes();
			        
			        for(int j=0; j<child.getLength(); j++) {
			        	Node nodeItem = child.item(j);
			        	if(nodeItem.getNodeName().equals("ImageDescriptor")) {
			        		int offsetX = Integer.valueOf(nodeItem.getAttributes().getNamedItem("imageLeftPosition").getNodeValue());
			        		int offsetY = Integer.valueOf(nodeItem.getAttributes().getNamedItem("imageTopPosition").getNodeValue());
			        		frame[i].setOffset(offsetX, offsetY);
			        		break;
			        	}
			        }
			    }
			    
			} catch (Exception e) {
			    throw new ResourceException(ResourceException.FILE_NOT_FOUND, url);
			}
			
		} else if(extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) {
			frame = new ImageData[1];
			
			try {
				frame[0] = new ImageData(ImageIO.read(loader.getResource(url)));
			} catch(Exception e) {
				throw new ResourceException(ResourceException.FILE_NOT_FOUND, url);
			}
		} else {
			throw new ResourceException(ResourceException.EXTENSION_INCORRECT, extension);
		}
		
		return frame;
	}
}
