(ns gdl.ui.text-button
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextButton)))

(defn create [text ^Skin skin]
  (TextButton. (str text) skin))
