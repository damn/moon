(ns gdl.window
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               Window)))

(defn create [title skin]
  (Window. ^String title ^Skin skin))
