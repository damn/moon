(ns file-handle.skin
  (:require [com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn f [file-handle]
  (skin/create file-handle))
