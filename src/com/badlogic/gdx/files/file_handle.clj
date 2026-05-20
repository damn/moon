(ns com.badlogic.gdx.files.file-handle
  (:require [gdl.files.file-handle :as file-handle])
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.graphics Pixmap
                                      Texture)
           (com.badlogic.gdx.graphics.g2d.freetype FreeTypeFontGenerator)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(extend-type FileHandle
  file-handle/FileHandle
  (list [this]
    (.list this))
  (path [this]
    (.path this))
  (extension [this]
    (.extension this))
  (directory? [this]
    (.isDirectory this))
  (texture [this]
    (Texture. this))

	;public Texture (FileHandle file) {
	;	this(file, null, false);
	;}

  ;	public Texture (FileHandle file, Format format, boolean useMipMaps) {
  ;		this(TextureData.Factory.loadFromFile(file, format, useMipMaps));
  ;	}

  ; gdx.graphics.TextureData

	;	public static TextureData loadFromFile (FileHandle file, Format format, boolean useMipMaps) {
	;		if (file == null) return null;
	;		if (file.name().endsWith(".cim")) return new FileTextureData(file, PixmapIO.readCIM(file), format, useMipMaps);
	;		if (file.name().endsWith(".etc1")) return new ETC1TextureData(file, useMipMaps);
	;		if (file.name().endsWith(".ktx") || file.name().endsWith(".zktx")) return new KTXTextureData(file, useMipMaps);
	;		return new FileTextureData(file, new Pixmap(file), format, useMipMaps);
	;	}

  ; com.badlogic.gdx.graphics.glutils.FileTextureData

	; public Texture (TextureData data) {
	; 	this(GL20.GL_TEXTURE_2D, Gdx.gl.glGenTexture(), data);
	; }

  (pixmap [this]
    (Pixmap. this))

;	/** Creates a new Pixmap instance from the given file. The file must be a Png, Jpeg or Bitmap. Paletted formats are not
;	 * supported.
;	 *
;	 * @param file the {@link FileHandle} */
;	public Pixmap (FileHandle file) {
;		try {
;			byte[] bytes = file.readBytes();
;			pixmap = new Gdx2DPixmap(bytes, 0, bytes.length, 0);
;		} catch (Exception e) {
;			throw new GdxRuntimeException("Couldn't load file: " + file, e);
;		}
;	}

;	/** Constructs a new Pixmap from a {@link Gdx2DPixmap}.
;	 * @param pixmap */
;	public Pixmap (Gdx2DPixmap pixmap) {
;		this.pixmap = pixmap;
;	}
;
  (skin [this]
    (Skin. this))

  (freetype-font-generator [this]
    (FreeTypeFontGenerator. this)))
