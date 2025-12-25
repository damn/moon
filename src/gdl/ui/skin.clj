(ns gdl.ui.skin
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn font [^Skin skin name]
  (.getFont skin name))

(defn create
  "Creates a skin containing the resources in the specified skin JSON file. If a file in the same directory with a \".atlas\" extension exists, it is loaded as a TextureAtlas and the texture regions added to the skin. The atlas is automatically disposed when the skin is disposed."
  [^FileHandle file-handle]
  (Skin. file-handle))
