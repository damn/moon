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

  (skin [this]
    (Skin. this))

  (freetype-font-generator [this]
    (FreeTypeFontGenerator. this)))
