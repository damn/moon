(ns clojure.com.badlogic.gdx.scenes.scene2d.ui.skin
  "A skin stores resources for UI widgets to use (texture regions, ninepatches, fonts, colors, etc). Resources are named and can
  be looked up by name and type. Resources can be described in JSON. Skin provides useful conversions, such as allowing access to
  regions in the atlas as ninepatches, sprites, drawables, etc. The get* methods return an instance of the object in the skin.
  The new* methods return a copy of an instance in the skin.
  
  See the documentation for more."
  (:import (com.badlogic.gdx.files FileHandle)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create
  "Creates a skin containing the resources in the specified skin JSON file. If a file in the same directory with a \".atlas\"
  extension exists, it is loaded as a texture atlas and the texture regions added to the skin. The atlas is
  automatically disposed when the skin is disposed."
  [^FileHandle file-handle]
  (Skin. file-handle))

(defn font [^Skin skin name]
  (.getFont skin name))
